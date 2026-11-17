package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Input validation helpers used by the forms and services.
 */
public final class ValidationUtil {

    public static final String[] ACADEMIC_YEARS = {"1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"};

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String NAME_PATTERN = "^[A-Za-z][A-Za-z .'-]*$";

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Method overloading: checks many values at once.
     * Returns true if ANY of the given values is blank.
     */
    public static boolean isBlank(String... values) {
        for (String value : values) {
            if (isBlank(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches(EMAIL_PATTERN);
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().matches(NAME_PATTERN);
    }

    /**
     * A password must be at least 6 characters and contain a letter and a digit.
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }
        return hasLetter && hasDigit;
    }

    public static boolean isValidAcademicYear(String year) {
        for (String allowed : ACADEMIC_YEARS) {
            if (allowed.equals(year)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts text in the format YYYY-MM-DD into a LocalDate.
     *
     * @throws IllegalArgumentException with a friendly message if the date is invalid
     */
    public static LocalDate parseDate(String text) {
        if (isBlank(text)) {
            throw new IllegalArgumentException("Please enter a target date.");
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date \"" + text.trim()
                    + "\". Please use the format YYYY-MM-DD, for example 2026-12-31.");
        }
    }
}
