package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.UserStorage;
import financemanager.utils.validators.UserValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controls the Login view.
 */
public class LoginController extends App {

    public TextField email;
    public TextField password;
    public Text feedback;

    private final UserStorage userStorage = new UserStorage();

    @FXML
    public void switchToDashboard(ActionEvent actionEvent) {
        try {
            App.currentUser = userStorage.read(email.getText());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to read the current user.");
            return;
        }

        try {
            App.setRoot("Dashboard");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    @FXML
    public void switchToRegister() {
        try {
            App.setRoot("Register");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        if (isValidInput())
            switchToDashboard(actionEvent);
    }

    private boolean isValidInput() {

        if (!UserValidator.emailExists(email.getText())) {
            feedback.setText("Wrong email or password. Please try again.");
            return false;
        }

        if (!UserValidator.passwordBelongsToEmail(email.getText(), password.getText())) {
            feedback.setText("Wrong email or password. Please try again.");
            return false;
        }


        return true;
    }
}