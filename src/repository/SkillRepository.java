package repository;

import model.SkillStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves each student's skill progress in data/progress.txt.
 *
 * File format (one line per student per skill that has been touched):
 *     email | skillId | status
 *
 * Only skills whose status was changed are stored. Any skill that is not in the
 * file is treated as "Not Started". Because each line contains the student's email,
 * every student only ever sees their own progress (user isolation).
 */
public class SkillRepository {

    private static final String FILE_NAME = "progress.txt";

    private final FileManager fileManager;

    public SkillRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Returns a HashMap of skillId -> status for one student.
     */
    public Map<String, SkillStatus> loadStatuses(String email) throws IOException {
        Map<String, SkillStatus> statuses = new HashMap<>();
        for (String[] record : fileManager.readRecords(FILE_NAME)) {
            if (record.length < 3 || !record[0].equalsIgnoreCase(email)) {
                continue;
            }
            try {
                statuses.put(record[1], SkillStatus.fromText(record[2]));
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping invalid progress record: " + e.getMessage());
            }
        }
        return statuses;
    }

    /**
     * Saves (inserts or replaces) the status of one skill for one student.
     */
    public void saveStatus(String email, String skillId, SkillStatus status) throws IOException {
        List<String[]> records = fileManager.readRecords(FILE_NAME);
        boolean updated = false;
        for (String[] record : records) {
            if (record.length >= 3 && record[0].equalsIgnoreCase(email) && record[1].equals(skillId)) {
                record[2] = status.name();
                updated = true;
            }
        }
        if (!updated) {
            records.add(new String[] {email, skillId, status.name()});
        }
        fileManager.writeRawRecords(FILE_NAME, records);
    }

    /**
     * Moves all progress lines to a new email address (used when a student changes email).
     */
    public void changeOwnerEmail(String oldEmail, String newEmail) throws IOException {
        List<String[]> records = new ArrayList<>(fileManager.readRecords(FILE_NAME));
        for (String[] record : records) {
            if (record.length >= 1 && record[0].equalsIgnoreCase(oldEmail)) {
                record[0] = newEmail;
            }
        }
        fileManager.writeRawRecords(FILE_NAME, records);
    }
}
