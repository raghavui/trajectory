package model;

import java.time.LocalDate;

/**
 * A personal learning goal created by a student,
 * for example "Finish Collections Framework by 15 Dec".
 */
public class Goal implements Persistable {

    public static final String PENDING = "Pending";
    public static final String COMPLETED = "Completed";

    private final String goalId;
    private String studentEmail;
    private String title;
    private String description;
    private LocalDate targetDate;
    private String status;

    public Goal(String goalId, String studentEmail, String title, String description,
                LocalDate targetDate, String status) {
        this.goalId = goalId;
        this.studentEmail = studentEmail;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        setStatus(status);
    }

    /**
     * A goal is overdue when it is still pending and its target date has passed.
     */
    public boolean isOverdue() {
        return !isCompleted() && targetDate.isBefore(LocalDate.now());
    }

    public boolean isCompleted() {
        return COMPLETED.equals(status);
    }

    /**
     * Text shown in the goals table, e.g. "Pending", "Completed" or "Overdue".
     */
    public String getDisplayStatus() {
        return isOverdue() ? "Overdue" : status;
    }

    public String getGoalId() {
        return goalId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!PENDING.equals(status) && !COMPLETED.equals(status)) {
            throw new IllegalArgumentException("Invalid goal status: " + status);
        }
        this.status = status;
    }

    /**
     * File format: goalId | email | title | description | targetDate | status
     */
    @Override
    public String[] toRecord() {
        return new String[] {goalId, studentEmail, title, description, targetDate.toString(), status};
    }

    public static Goal fromRecord(String[] fields) {
        if (fields.length < 6) {
            throw new IllegalArgumentException("Goal record has missing fields");
        }
        return new Goal(fields[0], fields[1], fields[2], fields[3], LocalDate.parse(fields[4]), fields[5]);
    }
}
