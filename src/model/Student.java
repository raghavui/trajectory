package model;

/**
 * A Student is a User who also has a college, an academic year and a selected career.
 *
 * Inheritance: Student "is a" User, so it reuses name, email and password fields.
 */
public class Student extends User {

    private String collegeName;
    private String currentYear;
    private String selectedCareer;   // empty string means "no career chosen yet"

    /**
     * Constructor used when a new student registers (no career selected yet).
     */
    public Student(String fullName, String email, String passwordHash, String salt,
                   String collegeName, String currentYear) {
        this(fullName, email, passwordHash, salt, collegeName, currentYear, "");
    }

    /**
     * Constructor used when a student is loaded back from the file.
     * (Two constructors with different parameters = constructor overloading.)
     */
    public Student(String fullName, String email, String passwordHash, String salt,
                   String collegeName, String currentYear, String selectedCareer) {
        super(fullName, email, passwordHash, salt);
        this.collegeName = collegeName;
        this.currentYear = currentYear;
        this.selectedCareer = selectedCareer == null ? "" : selectedCareer;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public boolean hasSelectedCareer() {
        return !selectedCareer.isEmpty();
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getSelectedCareer() {
        return selectedCareer;
    }

    public void setSelectedCareer(String selectedCareer) {
        this.selectedCareer = selectedCareer == null ? "" : selectedCareer;
    }

    /**
     * File format: email | passwordHash | salt | fullName | college | year | career
     */
    @Override
    public String[] toRecord() {
        return new String[] {
                getEmail(), getPasswordHash(), getSalt(), getFullName(),
                collegeName, currentYear, selectedCareer
        };
    }

    /**
     * Creates a Student object from the values read from one line of students.txt.
     */
    public static Student fromRecord(String[] fields) {
        if (fields.length < 7) {
            throw new IllegalArgumentException("Student record has missing fields");
        }
        return new Student(fields[3], fields[0], fields[1], fields[2],
                fields[4], fields[5], fields[6]);
    }
}
