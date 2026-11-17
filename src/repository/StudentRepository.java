package repository;

import model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads students from data/students.txt.
 */
public class StudentRepository {

    private static final String FILE_NAME = "students.txt";

    private final FileManager fileManager;

    public StudentRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public List<Student> findAll() throws IOException {
        List<Student> students = new ArrayList<>();
        for (String[] record : fileManager.readRecords(FILE_NAME)) {
            try {
                students.add(Student.fromRecord(record));
            } catch (IllegalArgumentException e) {
                // A damaged line should not stop the whole application from working
                System.err.println("Skipping invalid student record: " + e.getMessage());
            }
        }
        return students;
    }

    /**
     * Returns the student with this email, or null if nobody has registered with it.
     * Emails are compared without caring about upper/lower case.
     */
    public Student findByEmail(String email) throws IOException {
        for (Student student : findAll()) {
            if (student.getEmail().equalsIgnoreCase(email.trim())) {
                return student;
            }
        }
        return null;
    }

    public boolean emailExists(String email) throws IOException {
        return findByEmail(email) != null;
    }

    /**
     * Saves a NEW student by appending one line (existing students are untouched).
     */
    public void save(Student student) throws IOException {
        fileManager.appendRecord(FILE_NAME, student);
    }

    /**
     * Updates an existing student. The student is found using {@code originalEmail},
     * which allows the email itself to be changed from the profile screen.
     */
    public void update(String originalEmail, Student updatedStudent) throws IOException {
        List<Student> students = findAll();
        boolean found = false;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getEmail().equalsIgnoreCase(originalEmail)) {
                students.set(i, updatedStudent);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IOException("Student not found: " + originalEmail);
        }
        fileManager.writeRecords(FILE_NAME, students);
    }

    /**
     * Method overloading: same name "update", different parameters.
     * Used when the email has not changed.
     */
    public void update(Student student) throws IOException {
        update(student.getEmail(), student);
    }
}
