package service;

import model.Student;
import repository.ActivityRepository;
import repository.GoalRepository;
import repository.SkillRepository;
import repository.StudentRepository;
import util.PasswordUtil;
import util.ValidationException;
import util.ValidationUtil;

import java.io.IOException;

/**
 * Handles registration, login, logout and profile/account changes.
 * It also remembers which student is currently logged in (the "session").
 */
public class AuthenticationService {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;

    private Student currentStudent;   // null when nobody is logged in

    public AuthenticationService(StudentRepository studentRepository, SkillRepository skillRepository,
                                 GoalRepository goalRepository, ActivityRepository activityRepository) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.goalRepository = goalRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Validates the registration form and saves the new student.
     */
    public Student register(String fullName, String email, String password, String confirmPassword,
                            String collegeName, String currentYear) throws ValidationException, IOException {
        if (ValidationUtil.isBlank(fullName, email, password, confirmPassword, collegeName, currentYear)) {
            throw new ValidationException("All fields are required. Please fill in every field.");
        }
        if (!ValidationUtil.isValidName(fullName)) {
            throw new ValidationException("Please enter a valid full name (letters and spaces only).");
        }
        String cleanEmail = email.trim().toLowerCase();
        if (!ValidationUtil.isValidEmail(cleanEmail)) {
            throw new ValidationException("Please enter a valid email address, e.g. name@college.edu");
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            throw new ValidationException("Password must be at least 6 characters and contain a letter and a number.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Password and Confirm Password do not match.");
        }
        if (!ValidationUtil.isValidAcademicYear(currentYear)) {
            throw new ValidationException("Please select a valid academic year.");
        }
        if (studentRepository.emailExists(cleanEmail)) {
            throw new ValidationException("This email is already registered. Please log in instead.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        Student student = new Student(fullName.trim(), cleanEmail, hash, salt, collegeName.trim(), currentYear);
        studentRepository.save(student);
        activityRepository.log(cleanEmail, "Created a student account");
        return student;
    }

    /**
     * Checks the email and password. On success the student becomes the current session user.
     */
    public Student login(String email, String password) throws ValidationException, IOException {
        if (ValidationUtil.isBlank(email, password)) {
            throw new ValidationException("Please enter both email and password.");
        }
        Student student = studentRepository.findByEmail(email.trim());
        if (student == null) {
            throw new ValidationException("No account found with this email. Please register first.");
        }
        if (!PasswordUtil.verifyPassword(password, student.getSalt(), student.getPasswordHash())) {
            throw new ValidationException("Incorrect password. Please try again.");
        }
        currentStudent = student;
        return student;
    }

    /**
     * Clears the session. All saved data stays in the files.
     */
    public void logout() {
        currentStudent = null;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public boolean isLoggedIn() {
        return currentStudent != null;
    }

    /**
     * Updates name, college and academic year of the logged-in student.
     */
    public void updateProfile(String fullName, String collegeName, String currentYear)
            throws ValidationException, IOException {
        requireLogin();
        if (ValidationUtil.isBlank(fullName, collegeName, currentYear)) {
            throw new ValidationException("Name, college and academic year cannot be empty.");
        }
        if (!ValidationUtil.isValidName(fullName)) {
            throw new ValidationException("Please enter a valid full name (letters and spaces only).");
        }
        if (!ValidationUtil.isValidAcademicYear(currentYear)) {
            throw new ValidationException("Please select a valid academic year.");
        }
        currentStudent.setFullName(fullName.trim());
        currentStudent.setCollegeName(collegeName.trim());
        currentStudent.setCurrentYear(currentYear);
        studentRepository.update(currentStudent);
        activityRepository.log(currentStudent.getEmail(), "Updated profile details");
    }

    /**
     * Changes the registered email. For safety the current password must be confirmed,
     * the new email must be valid and must not belong to another student.
     * All progress, goals and activities are moved to the new email.
     */
    public void changeEmail(String newEmail, String currentPassword) throws ValidationException, IOException {
        requireLogin();
        if (ValidationUtil.isBlank(newEmail, currentPassword)) {
            throw new ValidationException("Please enter the new email and your current password.");
        }
        String cleanEmail = newEmail.trim().toLowerCase();
        String oldEmail = currentStudent.getEmail();
        if (cleanEmail.equals(oldEmail)) {
            throw new ValidationException("The new email is the same as your current email.");
        }
        if (!ValidationUtil.isValidEmail(cleanEmail)) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (!PasswordUtil.verifyPassword(currentPassword, currentStudent.getSalt(), currentStudent.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect. Email was not changed.");
        }
        if (studentRepository.emailExists(cleanEmail)) {
            throw new ValidationException("This email is already used by another account.");
        }
        currentStudent.setEmail(cleanEmail);
        studentRepository.update(oldEmail, currentStudent);
        skillRepository.changeOwnerEmail(oldEmail, cleanEmail);
        goalRepository.changeOwnerEmail(oldEmail, cleanEmail);
        activityRepository.changeOwnerEmail(oldEmail, cleanEmail);
        activityRepository.log(cleanEmail, "Changed registered email address");
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword)
            throws ValidationException, IOException {
        requireLogin();
        if (ValidationUtil.isBlank(currentPassword, newPassword, confirmPassword)) {
            throw new ValidationException("Please fill in all password fields.");
        }
        if (!PasswordUtil.verifyPassword(currentPassword, currentStudent.getSalt(), currentStudent.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new ValidationException("New password must be at least 6 characters and contain a letter and a number.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("New password and confirmation do not match.");
        }
        String salt = PasswordUtil.generateSalt();
        currentStudent.setPassword(PasswordUtil.hashPassword(newPassword, salt), salt);
        studentRepository.update(currentStudent);
        activityRepository.log(currentStudent.getEmail(), "Changed account password");
    }

    private void requireLogin() throws ValidationException {
        if (currentStudent == null) {
            throw new ValidationException("Please log in first.");
        }
    }
}
