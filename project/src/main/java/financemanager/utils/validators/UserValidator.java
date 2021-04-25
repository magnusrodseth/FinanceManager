package financemanager.utils.validators;

import financemanager.models.storage.UserStorage;
import financemanager.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Validates properties related to User objects.
 */
public class UserValidator {
    public static boolean isValidId(int id) {
        return id > 0;
    }

    public static boolean isValidName(String name) {
        return name.matches("^[a-zA-Z]+");
    }

    public static boolean isValidEmail(String email) {
        // Source: https://stackoverflow.com/questions/8204680/java-regex-email
        Pattern emailRegex = Pattern.compile(
                "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE
        );

        return emailRegex.matcher(email).find();
    }

    public static boolean isValidPassword(String password) {
        if (!hasValidCharacters(password))
            return false;

        return isValidLength(password);
    }

    public static boolean emailExists(String email) {
        List<User> users = new ArrayList<>();
        UserStorage userStorage = new UserStorage();

        try {
            users = userStorage.getUsers();
        } catch (IOException e) {
            // If an error occurs, a validator failed.
            return false;
        }

        // This List holds all existing user emails
        List<String> emailList = users.stream().map(User::getEmail).collect(Collectors.toList());

        return emailList.contains(email);
    }

    public static boolean isVacantEmail(String email) {
        UserStorage userStorage = new UserStorage();

        try {
            return userStorage.read(email) == null;
        } catch (IOException e) {
            // If this throws, the validation has not failed but the read from file failed
            return false;
        }
    }


    public static boolean passwordBelongsToEmail(String email, String password) {

        UserStorage userStorage = new UserStorage();
        User user = null;
        try {
            user = userStorage.read(email);
        } catch (IOException e) {
            // If this throws, the validation has not failed but the read from file failed
            return false;
        }

        // User cannot be null and validate password based on the supplied email
        return user != null && user.getPassword().equals(password);
    }

    public static boolean isValidLength(String password) {
        return password.length() >= 6;
    }

    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public static boolean hasValidCharacters(String password) {
        // This regex allows 'A' -> 'Z', numbers and special characters.
        return password.matches("^[A-Za-z0-9_*!\"#$%&/()=?+^.,<>'@]*$");
    }
}
