package financemanager.models;

import financemanager.models.storage.UserStorage;
import financemanager.utils.validators.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UserTest {

    @Test
    @DisplayName("ID test")
    void idTest() {
        // User id must be greater than 0
        Assertions.assertFalse(UserValidator.isValidId(-100));

        // User id must be greater than 0
        Assertions.assertFalse(UserValidator.isValidId(0));

        // Valid user id passes
        Assertions.assertTrue(UserValidator.isValidId(123));
    }

    @Test
    @DisplayName("Name test")
    void nameTest() {
        // Illegal characters should return false
        Assertions.assertFalse(UserValidator.isValidName("Øyvind"));

        // Illegal characters should return false
        Assertions.assertFalse(UserValidator.isValidName("æææ"));

        // Illegal characters should return false
        Assertions.assertFalse(UserValidator.isValidName("æøe2åø4å321"));

        // Valid name passes
        Assertions.assertTrue(UserValidator.isValidName("John"));

        // Valid name passes
        Assertions.assertTrue(UserValidator.isValidName("Adam"));

        // Valid name passes
        Assertions.assertTrue(UserValidator.isValidName("Doe"));

        // Valid name passes
        Assertions.assertTrue(UserValidator.isValidName("Nordmann"));
    }

    @Test
    @DisplayName("E-mail test")
    void emailTest() {
        // Illegal characters in e-mail
        Assertions.assertFalse(UserValidator.isValidEmail("æøå.-*/@example.com"));

        // Valid e-mail passes
        Assertions.assertTrue(UserValidator.isValidEmail("john.doe@gmail.com"));
    }

    @Test
    @DisplayName("E-mail exists test")
    void emailExistsTest() {
        // Initialize
        UserStorage userStorage = new UserStorage();
        final int MOCK_ID = (int) Math.floor(Math.random() * 99999 + 1);
        final String MOCK_EMAIL = "john.doe@gmail.com";
        User MOCK_USER = new User(MOCK_ID, "John", "Doe", MOCK_EMAIL, "password");

        // Create new mock user
        try {
            userStorage.create(MOCK_USER);
        } catch (IOException e) {
            Assertions.assertThrows(IOException.class, () -> userStorage.create(MOCK_USER));
        }

        // E-mail exists, and should thus return true
        Assertions.assertTrue(UserValidator.emailExists(MOCK_EMAIL));

        // E-mail is taken, and should thus return false
        Assertions.assertFalse(UserValidator.isVacantEmail(MOCK_EMAIL));

        // Clean up after test
        try {
            userStorage.delete(MOCK_EMAIL);
        } catch (IOException e) {
            Assertions.assertThrows(IOException.class, () -> userStorage.delete(MOCK_EMAIL));
        }
    }

    @Test
    @DisplayName("Password test")
    void passwordTest() {
        // Password must be at least 6 characters long
        Assertions.assertFalse(UserValidator.isValidPassword("abcd"));

        // Password contains illegal characters
        Assertions.assertFalse(UserValidator.isValidPassword("abcdæa"));

        // Valid password passes
        Assertions.assertTrue(UserValidator.isValidPassword("abcdefghi"));

        // Unequal passwords returns false
        Assertions.assertFalse(UserValidator.passwordsMatch("Jostein", "Tysse123"));

        // Unequal passwords returns false
        Assertions.assertFalse(UserValidator.passwordsMatch("Jostein", "jostein"));

        // Valid password returns true
        Assertions.assertTrue(UserValidator.passwordsMatch("Password123", "Password123"));
    }

    @Test
    @DisplayName("Password exists test")
    void passwordExistsTest() {
        // Initialize
        UserStorage userStorage = new UserStorage();
        final int MOCK_ID = (int) Math.floor(Math.random() * 99999 + 1);
        final String MOCK_PASSWORD = "this_is_a_very_safe_password***12345//%";
        final String MOCK_EMAIL = "feioqhiohqwef.webniqtuw@gmail.com";
        User MOCK_USER = new User(MOCK_ID, "Adam", "Smith", MOCK_EMAIL, MOCK_PASSWORD);

        // Create new mock user
        try {
            userStorage.create(MOCK_USER);
        } catch (IOException e) {
            Assertions.assertThrows(IOException.class, () -> userStorage.create(MOCK_USER));
        }

        // Valid password passes
        Assertions.assertTrue(UserValidator.isValidPassword(MOCK_PASSWORD));

        // Password exists, and should thus return true
        Assertions.assertTrue(UserValidator.passwordBelongsToEmail(MOCK_EMAIL, MOCK_PASSWORD));

        // Equal passwords must return true
        Assertions.assertTrue(UserValidator.passwordsMatch(MOCK_PASSWORD, MOCK_PASSWORD));

        // Cleanup
        try {
            userStorage.delete(MOCK_EMAIL);
        } catch (IOException e) {
            Assertions.assertThrows(IOException.class, () -> userStorage.delete(MOCK_EMAIL));
        }

    }

}
