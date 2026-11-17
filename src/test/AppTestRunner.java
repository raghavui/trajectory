package test;

import model.Goal;
import model.LearningPath;
import model.Skill;
import model.SkillStatus;
import model.Student;
import repository.FileManager;
import service.AppServices;
import service.AuthenticationService;
import util.ValidationException;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Simple test program (no external testing library needed).
 *
 * It runs the 14 test cases from TEST_CASES.md against a separate "test-data" folder,
 * so your real data in the "data" folder is never touched.
 * Run it like any Java program: it prints PASS or FAIL for every test.
 */
public class AppTestRunner {

    private static final String TEST_FOLDER = "test-data";
    private static int passed = 0;
    private static int failed = 0;

    /**
     * A small interface so each test can be written as a lambda that may throw an exception.
     */
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        deleteFolder(new File(TEST_FOLDER));
        AppServices services = new AppServices(TEST_FOLDER);
        AuthenticationService auth = services.getAuthenticationService();
        String email = "riya.sharma@college.edu";
        String password = "secret123";

        test("TC01 Successful registration", () -> {
            Student s = auth.register("Riya Sharma", email, password, password, "ABC Institute", "3rd Year");
            check(s.getEmail().equals(email), "email saved");
            check(!s.getPasswordHash().contains(password), "password is not stored as plain text");
        });

        test("TC02 Duplicate email registration is rejected", () ->
                expectValidationError(() -> auth.register("Other Person", "RIYA.SHARMA@college.edu",
                        "abc12345", "abc12345", "XYZ College", "1st Year")));

        test("TC03 Successful login", () -> {
            Student s = auth.login(email, password);
            check(auth.isLoggedIn() && s.getFullName().equals("Riya Sharma"), "student is logged in");
        });

        test("TC04 Incorrect password is rejected", () ->
                expectValidationError(() -> new AppServices(TEST_FOLDER).getAuthenticationService()
                        .login(email, "wrong999")));

        test("TC05 Empty registration fields are rejected", () -> {
            expectValidationError(() -> auth.register("", "", "", "", "", "1st Year"));
            expectValidationError(() -> auth.register("Aman", "not-an-email", "abc12345", "abc12345", "X", "1st Year"));
            expectValidationError(() -> auth.register("Aman", "aman@x.com", "abc12345", "different1", "X", "1st Year"));
        });

        test("TC06 Career selection is saved", () -> {
            services.getCareerService().selectCareer(auth.getCurrentStudent(), "Java Developer");
            check("Java Developer".equals(auth.getCurrentStudent().getSelectedCareer()), "career set");
            expectValidationError(() -> services.getCareerService().selectCareer(auth.getCurrentStudent(), "Astronaut"));
        });

        test("TC07 Skill recommendation returns the career's skills by level", () -> {
            LearningPath path = services.getRecommendationService().generateLearningPath(auth.getCurrentStudent());
            check(path.getSkillsForLevel(Skill.BEGINNER).size() == 7, "7 beginner skills");
            check(path.getSkillsForLevel(Skill.INTERMEDIATE).size() == 8, "8 intermediate skills");
            check(path.getSkillsForLevel(Skill.ADVANCED).size() == 6, "6 advanced skills");
            check(path.getSkillsForLevel(Skill.BEGINNER).get(0).getSkillName().equals("Java Introduction"), "first skill");
        });

        test("TC08 Updating skill status is saved permanently", () -> {
            LearningPath path = services.getRecommendationService().generateLearningPath(auth.getCurrentStudent());
            List<Skill> skills = path.getAllSkills();
            for (int i = 0; i < 4; i++) {
                services.getProgressTracker().updateSkillStatus(email, skills.get(i), SkillStatus.COMPLETED);
            }
            services.getProgressTracker().updateSkillStatus(email, skills.get(4), SkillStatus.IN_PROGRESS);
            LearningPath reloaded = services.getRecommendationService().generateLearningPath(auth.getCurrentStudent());
            check(reloaded.getAllSkills().get(0).isCompleted(), "status reloaded from file");
            check(reloaded.getAllSkills().get(4).getStatus() == SkillStatus.IN_PROGRESS, "in-progress reloaded");
            boolean invalidRejected = false;
            try {
                SkillStatus.fromText("Done!!");
            } catch (IllegalArgumentException e) {
                invalidRejected = true;
            }
            check(invalidRejected, "invalid status text rejected");
        });

        test("TC09 Progress percentage calculation", () -> {
            check(services.getProgressTracker().calculatePercentage(4, 10) == 40, "4/10 = 40%");
            check(services.getProgressTracker().calculatePercentage(0, 0) == 0, "no skills = 0%");
            LearningPath path = services.getRecommendationService().generateLearningPath(auth.getCurrentStudent());
            check(services.getProgressTracker().calculatePercentage(path) == 19, "4/21 = 19%");
        });

        final String[] goalId = new String[1];
        test("TC10 Adding a learning goal", () -> {
            Goal goal = services.getGoalManager().addGoal(email, "Finish OOP | part 1",
                    "Line one\nLine two with \\ backslash", LocalDate.now().plusDays(10).toString());
            goalId[0] = goal.getGoalId();
            check(services.getGoalManager().getPendingGoals(email).size() == 1, "1 pending goal");
            expectValidationError(() -> services.getGoalManager().addGoal(email, "Bad date", "", "2026-02-30"));
            expectValidationError(() -> services.getGoalManager().addGoal(email, "Past", "", "2001-01-01"));
            expectValidationError(() -> services.getGoalManager().addGoal(email, "  ", "", "2099-01-01"));
        });

        test("TC11 Updating a goal and marking it completed", () -> {
            services.getGoalManager().updateGoal(email, goalId[0], "Finish OOP", "Updated",
                    LocalDate.now().plusDays(20).toString());
            Goal goal = services.getGoalManager().getGoals(email).get(0);
            check(goal.getTitle().equals("Finish OOP") && goal.getDescription().equals("Updated"), "goal updated");
            services.getGoalManager().markCompleted(email, goalId[0]);
            check(services.getGoalManager().getPendingGoals(email).isEmpty(), "no pending goals left");
        });

        test("TC12 Deleting a goal", () -> {
            Goal second = services.getGoalManager().addGoal(email, "Temp goal", "", LocalDate.now().toString());
            services.getGoalManager().deleteGoal(email, second.getGoalId());
            check(services.getGoalManager().getGoals(email).size() == 1, "goal removed");
            // another student cannot delete Riya's goal (user isolation)
            expectValidationError(() -> services.getGoalManager().deleteGoal("someone@else.com", goalId[0]));
        });

        test("TC13 Logging out clears the session", () -> {
            auth.logout();
            check(!auth.isLoggedIn() && auth.getCurrentStudent() == null, "session cleared");
        });

        test("TC14 Data is still there after restarting the application", () -> {
            AppServices restarted = new AppServices(TEST_FOLDER);   // simulates closing and reopening the app
            Student s = restarted.getAuthenticationService().login(email, password);
            check(s.getSelectedCareer().equals("Java Developer"), "career kept");
            LearningPath path = restarted.getRecommendationService().generateLearningPath(s);
            check(restarted.getProgressTracker().calculatePercentage(path) == 19, "progress kept");
            Goal goal = restarted.getGoalManager().getGoals(email).get(0);
            check(goal.isCompleted() && goal.getTitle().equals("Finish OOP"), "goal kept");
        });

        test("EXTRA Special characters survive save and load", () -> {
            String[] original = {"a|b", "line1\nline2", "back\\slash", ""};
            String[] loaded = FileManager.splitLine(FileManager.joinFields(original));
            check(loaded.length == original.length, "same field count");
            for (int i = 0; i < original.length; i++) {
                check(original[i].equals(loaded[i]), "field " + i + " unchanged");
            }
        });

        test("EXTRA Changing email keeps progress and goals", () -> {
            AppServices fresh = new AppServices(TEST_FOLDER);
            fresh.getAuthenticationService().login(email, password);
            expectValidationError(() -> fresh.getAuthenticationService().changeEmail("riya.new@college.edu", "wrong1"));
            fresh.getAuthenticationService().changeEmail("riya.new@college.edu", password);
            Student s = fresh.getAuthenticationService().getCurrentStudent();
            LearningPath path = fresh.getRecommendationService().generateLearningPath(s);
            check(fresh.getProgressTracker().calculatePercentage(path) == 19, "progress moved");
            check(fresh.getGoalManager().getGoals("riya.new@college.edu").size() == 1, "goals moved");
        });

        test("EXTRA Switching career and back restores old progress", () -> {
            AppServices app = new AppServices(TEST_FOLDER);
            Student s = app.getAuthenticationService().login("riya.new@college.edu", password);
            app.getCareerService().selectCareer(s, "Web Developer");
            LearningPath web = app.getRecommendationService().generateLearningPath(s);
            check(app.getProgressTracker().calculatePercentage(web) == 0, "new career starts at 0%");
            app.getCareerService().selectCareer(s, "Java Developer");
            LearningPath java = app.getRecommendationService().generateLearningPath(s);
            check(app.getProgressTracker().calculatePercentage(java) == 19, "Java progress is back");
        });

        test("EXTRA Two students see only their own data", () -> {
            AppServices app = new AppServices(TEST_FOLDER);
            app.getAuthenticationService().register("Aman Verma", "aman@college.edu", "pass1234", "pass1234",
                    "XYZ College", "1st Year");
            Student aman = app.getAuthenticationService().login("aman@college.edu", "pass1234");
            check(!aman.hasSelectedCareer(), "Aman has no career yet");
            check(app.getGoalManager().getGoals("aman@college.edu").isEmpty(), "Aman has no goals");
            app.getCareerService().selectCareer(aman, "Java Developer");
            LearningPath path = app.getRecommendationService().generateLearningPath(aman);
            check(app.getProgressTracker().calculatePercentage(path) == 0, "Riya's progress not visible to Aman");
        });

        test("EXTRA Profile update and password change", () -> {
            AppServices app = new AppServices(TEST_FOLDER);
            AuthenticationService a = app.getAuthenticationService();
            a.login("aman@college.edu", "pass1234");
            a.updateProfile("Aman K Verma", "XYZ College of Engineering", "2nd Year");
            expectValidationError(() -> a.updateProfile("Aman", "XYZ", "9th Year"));
            expectValidationError(() -> a.changePassword("wrongpass1", "newpass99", "newpass99"));
            a.changePassword("pass1234", "newpass99", "newpass99");
            AppServices restarted = new AppServices(TEST_FOLDER);
            Student s = restarted.getAuthenticationService().login("aman@college.edu", "newpass99");
            check(s.getFullName().equals("Aman K Verma") && s.getCurrentYear().equals("2nd Year"), "profile saved");
        });

        test("EXTRA Overdue goal is detected", () -> {
            Goal old = new Goal("G9999", "x@y.com", "Old goal", "", LocalDate.now().minusDays(1), Goal.PENDING);
            check(old.isOverdue() && old.getDisplayStatus().equals("Overdue"), "pending + past date = overdue");
        });

        test("EXTRA Damaged line in a data file is skipped", () -> {
            try (java.io.FileWriter writer = new java.io.FileWriter(new File(TEST_FOLDER, "students.txt"), true)) {
                writer.write("this line is broken\n");
            }
            AppServices app = new AppServices(TEST_FOLDER);
            Student s = app.getAuthenticationService().login("aman@college.edu", "newpass99");
            check(s != null, "valid students still load");
        });

        System.out.println();
        System.out.println("Result: " + passed + " passed, " + failed + " failed");
        deleteFolder(new File(TEST_FOLDER));
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void test(String name, TestCase testCase) {
        try {
            testCase.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable e) {
            failed++;
            System.out.println("FAIL  " + name + "  ->  " + e.getMessage());
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("Check failed: " + description);
        }
    }

    private static void expectValidationError(TestCase action) throws Exception {
        try {
            action.run();
        } catch (ValidationException expected) {
            return;   // this is what we wanted
        }
        throw new AssertionError("Expected a validation error, but none was thrown");
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFolder(file);
            }
        }
        folder.delete();
    }
}
