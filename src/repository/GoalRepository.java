package repository;

import model.Goal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads learning goals from data/goals.txt.
 */
public class GoalRepository {

    private static final String FILE_NAME = "goals.txt";

    private final FileManager fileManager;

    public GoalRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Returns the goals of ALL students (used internally when rewriting the file).
     */
    public List<Goal> findAll() throws IOException {
        List<Goal> goals = new ArrayList<>();
        for (String[] record : fileManager.readRecords(FILE_NAME)) {
            try {
                goals.add(Goal.fromRecord(record));
            } catch (RuntimeException e) {
                // covers IllegalArgumentException and DateTimeParseException
                System.err.println("Skipping invalid goal record: " + e.getMessage());
            }
        }
        return goals;
    }

    /**
     * Returns only the goals that belong to one student (user isolation).
     */
    public List<Goal> findByStudent(String email) throws IOException {
        List<Goal> result = new ArrayList<>();
        for (Goal goal : findAll()) {
            if (goal.getStudentEmail().equalsIgnoreCase(email)) {
                result.add(goal);
            }
        }
        return result;
    }

    public void add(Goal goal) throws IOException {
        fileManager.appendRecord(FILE_NAME, goal);
    }

    public void update(Goal updatedGoal) throws IOException {
        List<Goal> goals = findAll();
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getGoalId().equals(updatedGoal.getGoalId())) {
                goals.set(i, updatedGoal);
                fileManager.writeRecords(FILE_NAME, goals);
                return;
            }
        }
        throw new IOException("Goal not found: " + updatedGoal.getGoalId());
    }

    public void delete(String goalId) throws IOException {
        List<Goal> goals = findAll();
        boolean removed = goals.removeIf(goal -> goal.getGoalId().equals(goalId));
        if (!removed) {
            throw new IOException("Goal not found: " + goalId);
        }
        fileManager.writeRecords(FILE_NAME, goals);
    }

    /**
     * Creates the next unique goal ID such as "G1004" by looking at the highest ID in the file.
     */
    public String generateNextGoalId() throws IOException {
        int highest = 1000;
        for (Goal goal : findAll()) {
            try {
                int number = Integer.parseInt(goal.getGoalId().substring(1));
                highest = Math.max(highest, number);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignore IDs that are not in the "G1234" format
            }
        }
        return "G" + (highest + 1);
    }

    public void changeOwnerEmail(String oldEmail, String newEmail) throws IOException {
        List<Goal> goals = findAll();
        for (Goal goal : goals) {
            if (goal.getStudentEmail().equalsIgnoreCase(oldEmail)) {
                goal.setStudentEmail(newEmail);
            }
        }
        fileManager.writeRecords(FILE_NAME, goals);
    }
}
