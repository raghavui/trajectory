package service;

import model.Goal;
import repository.ActivityRepository;
import repository.GoalRepository;
import util.ValidationException;
import util.ValidationUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Add, view, update, complete and delete personal learning goals.
 * Every method checks that the goal belongs to the given student (user isolation).
 */
public class GoalManager {

    private static final int MAX_TITLE_LENGTH = 80;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;

    public GoalManager(GoalRepository goalRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.activityRepository = activityRepository;
    }

    public Goal addGoal(String email, String title, String description, String targetDateText)
            throws ValidationException, IOException {
        validateText(title, description);
        LocalDate targetDate = parseTargetDate(targetDateText);
        if (targetDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Target date cannot be in the past.");
        }
        Goal goal = new Goal(goalRepository.generateNextGoalId(), email, title.trim(),
                description == null ? "" : description.trim(), targetDate, Goal.PENDING);
        goalRepository.add(goal);
        activityRepository.log(email, "Added goal \"" + goal.getTitle() + "\"");
        return goal;
    }

    public Goal updateGoal(String email, String goalId, String title, String description, String targetDateText)
            throws ValidationException, IOException {
        Goal goal = findOwnGoal(email, goalId);
        validateText(title, description);
        LocalDate targetDate = parseTargetDate(targetDateText);
        // A past date is only allowed if the student kept the goal's original date unchanged
        if (!targetDate.equals(goal.getTargetDate()) && targetDate.isBefore(LocalDate.now())) {
            throw new ValidationException("The new target date cannot be in the past.");
        }
        goal.setTitle(title.trim());
        goal.setDescription(description == null ? "" : description.trim());
        goal.setTargetDate(targetDate);
        goalRepository.update(goal);
        activityRepository.log(email, "Updated goal \"" + goal.getTitle() + "\"");
        return goal;
    }

    public void markCompleted(String email, String goalId) throws ValidationException, IOException {
        Goal goal = findOwnGoal(email, goalId);
        if (goal.isCompleted()) {
            throw new ValidationException("This goal is already completed.");
        }
        goal.setStatus(Goal.COMPLETED);
        goalRepository.update(goal);
        activityRepository.log(email, "Completed goal \"" + goal.getTitle() + "\"");
    }

    public void deleteGoal(String email, String goalId) throws ValidationException, IOException {
        Goal goal = findOwnGoal(email, goalId);
        goalRepository.delete(goalId);
        activityRepository.log(email, "Deleted goal \"" + goal.getTitle() + "\"");
    }

    public List<Goal> getGoals(String email) throws IOException {
        return goalRepository.findByStudent(email);
    }

    public List<Goal> getPendingGoals(String email) throws IOException {
        List<Goal> pending = new ArrayList<>();
        for (Goal goal : getGoals(email)) {
            if (!goal.isCompleted()) {
                pending.add(goal);
            }
        }
        return pending;
    }

    public int countActiveGoals(String email) throws IOException {
        return getPendingGoals(email).size();
    }

    // ---------- helper methods ----------

    private Goal findOwnGoal(String email, String goalId) throws ValidationException, IOException {
        for (Goal goal : goalRepository.findByStudent(email)) {
            if (goal.getGoalId().equals(goalId)) {
                return goal;
            }
        }
        throw new ValidationException("Goal not found. Please select one of your goals.");
    }

    private void validateText(String title, String description) throws ValidationException {
        if (ValidationUtil.isBlank(title)) {
            throw new ValidationException("Goal title cannot be empty.");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new ValidationException("Goal title must be at most " + MAX_TITLE_LENGTH + " characters.");
        }
        if (description != null && description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description must be at most " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
    }

    private LocalDate parseTargetDate(String text) throws ValidationException {
        try {
            return ValidationUtil.parseDate(text);
        } catch (IllegalArgumentException e) {
            // convert to our own checked exception so the GUI shows the message
            throw new ValidationException(e.getMessage());
        }
    }
}
