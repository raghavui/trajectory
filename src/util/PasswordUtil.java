package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing using only Java's built-in security classes.
 *
 * How it works:
 * 1. A random "salt" (16 random bytes) is created for every student.
 * 2. The salt and password are combined and hashed with SHA-256.
 * 3. The hash is repeated many times ("key stretching") to make guessing slower.
 * 4. Only the salt and final hash are saved in the file - never the real password.
 *
 * At login, the same steps are repeated with the typed password and the saved salt.
 * If the result equals the saved hash, the password is correct.
 */
public final class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;

    private PasswordUtil() {
        // utility class: no objects needed
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = (salt + password).getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < ITERATIONS; i++) {
                digest.reset();
                hash = digest.digest(hash);
            }
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is available in every standard Java installation
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        String actualHash = hashPassword(password, salt);
        // MessageDigest.isEqual compares in constant time (safer than equals)
        return MessageDigest.isEqual(
                actualHash.getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8));
    }
}
