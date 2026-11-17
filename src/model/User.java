package model;

/**
 * Abstract base class for any person who can log in to the application.
 *
 * It holds the common login details (name, email, password hash, salt).
 * It is abstract because we never create a plain "User" - we always create a
 * specific type of user such as a Student.
 */
public abstract class User implements Persistable {

    // Encapsulation: fields are private and can only be accessed through methods
    private String fullName;
    private String email;
    private String passwordHash;
    private String salt;

    protected User(String fullName, String email, String passwordHash, String salt) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    /**
     * Abstract method: every type of user must describe its own role.
     * Student overrides this method (method overriding / polymorphism).
     */
    public abstract String getRole();

    /**
     * Returns the first name, used in friendly messages like "Welcome, Riya".
     */
    public String getFirstName() {
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex > 0 ? trimmed.substring(0, spaceIndex) : trimmed;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setPassword(String passwordHash, String salt) {
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return getRole() + ": " + fullName + " <" + email + ">";
    }
}
