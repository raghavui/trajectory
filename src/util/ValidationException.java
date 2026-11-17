package util;

/**
 * Custom checked exception thrown when the user enters invalid data,
 * for example an invalid email or a duplicate registration.
 *
 * The message of this exception is always written so it can be shown
 * directly to the user in a dialog box.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
