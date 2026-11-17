package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One entry in the "Recent learning activities" list on the dashboard,
 * for example "Marked 'Loops' as Completed".
 */
public class Activity implements Persistable {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM, hh:mm a");

    private String studentEmail;
    private final LocalDateTime time;
    private final String message;

    public Activity(String studentEmail, LocalDateTime time, String message) {
        this.studentEmail = studentEmail;
        this.time = time;
        this.message = message;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedTime() {
        return time.format(DISPLAY_FORMAT);
    }

    /**
     * File format: email | dateTime | message
     */
    @Override
    public String[] toRecord() {
        return new String[] {studentEmail, time.toString(), message};
    }

    public static Activity fromRecord(String[] fields) {
        if (fields.length < 3) {
            throw new IllegalArgumentException("Activity record has missing fields");
        }
        return new Activity(fields[0], LocalDateTime.parse(fields[1]), fields[2]);
    }
}
