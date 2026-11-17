package repository;

import model.Activity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the "recent learning activities" log in data/activities.txt.
 */
public class ActivityRepository {

    private static final String FILE_NAME = "activities.txt";

    private final FileManager fileManager;

    public ActivityRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void add(Activity activity) throws IOException {
        fileManager.appendRecord(FILE_NAME, activity);
    }

    /**
     * Shortcut used by the services: records a message with the current date and time.
     */
    public void log(String email, String message) throws IOException {
        add(new Activity(email, LocalDateTime.now(), message));
    }

    private List<Activity> findAll() throws IOException {
        List<Activity> activities = new ArrayList<>();
        for (String[] record : fileManager.readRecords(FILE_NAME)) {
            try {
                activities.add(Activity.fromRecord(record));
            } catch (RuntimeException e) {
                System.err.println("Skipping invalid activity record: " + e.getMessage());
            }
        }
        return activities;
    }

    /**
     * Returns the latest activities of one student, newest first.
     */
    public List<Activity> findRecent(String email, int limit) throws IOException {
        List<Activity> all = findAll();
        List<Activity> result = new ArrayList<>();
        // Lines are stored oldest -> newest, so we loop backwards to get newest first
        for (int i = all.size() - 1; i >= 0 && result.size() < limit; i--) {
            if (all.get(i).getStudentEmail().equalsIgnoreCase(email)) {
                result.add(all.get(i));
            }
        }
        return result;
    }

    public void changeOwnerEmail(String oldEmail, String newEmail) throws IOException {
        List<Activity> all = findAll();
        for (Activity activity : all) {
            if (activity.getStudentEmail().equalsIgnoreCase(oldEmail)) {
                activity.setStudentEmail(newEmail);
            }
        }
        fileManager.writeRecords(FILE_NAME, all);
    }
}
