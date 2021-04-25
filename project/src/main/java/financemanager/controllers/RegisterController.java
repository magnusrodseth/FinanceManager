package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.storage.UserStorage;
import financemanager.models.Account;
import financemanager.models.User;
import financemanager.utils.Tools;
import financemanager.utils.validators.UserValidator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controls the Register view.
 */
public class RegisterController extends App {
    public TextField firstName;
    public TextField lastName;
    public TextField email;
    public TextField password;
    public TextField confirmPassword;
    public Text feedback;

    private final UserStorage userStorage = new UserStorage();
    private final AccountStorage accountStorage = new AccountStorage();

    @FXML
    public void switchToLogin() {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    @FXML
    public void signUp() throws IOException {
        int lastUserId = userStorage.getLastUserId();
        int currentUserId = ++lastUserId;

        if (isValidInput()) {
            try {
                userStorage.create(new User(
                        currentUserId, firstName.getText(), lastName.getText(), email.getText().toLowerCase(), password.getText()
                ));
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to create a new user");
                return;
            }
            try {
                accountStorage.create(new Account(
                        currentUserId, firstName.getText() + "'s Spending Account",
                        Tools.generateAccountNumber(), 0
                ));
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to create a new account");
                return;
            }

            feedback.setText("Successfully created user. You can now login.");
        }
    }

    private boolean isValidInput() {
        if (!UserValidator.isValidName(firstName.getText())) {
            feedback.setText("First name must only have letters 'A' through 'Z'. Please try again.");
            return false;
        }

        if (!UserValidator.isValidName(lastName.getText())) {
            feedback.setText("Last name must only have letters 'A' through 'Z'. Please try again.");
            return false;
        }

        if (!UserValidator.isValidEmail(email.getText())) {
            feedback.setText("E-mail must only have letters 'A' through 'Z', a '.', an '@' and a valid TLD extension. Please try again.");
            return false;
        }

        if (!UserValidator.isVacantEmail(email.getText())) {
            feedback.setText("Email is already in use. Please try again.");
            return false;
        }

        if (!UserValidator.isValidLength(password.getText()) && !UserValidator.isValidLength(confirmPassword.getText())) {
            feedback.setText("Password must be 6 or more characters long. Please try again.");
            return false;
        }

        if (!UserValidator.passwordsMatch(password.getText(), confirmPassword.getText())) {
            feedback.setText("Passwords must match. Please try again.");
            return false;
        }

        if (!UserValidator.hasValidCharacters(password.getText())) {
            feedback.setText("Password must only have letters 'A' through 'Z', numbers and special characters. Please try again.");
            return false;
        }

        return true;
    }
}
