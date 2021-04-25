package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.Account;
import financemanager.utils.Tools;
import financemanager.utils.validators.AccountValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

/**
 * Controls the Account view.
 */
public class AccountsController extends App {
    public ScrollPane scrollPane;
    public TextField accountName;
    public Text feedback;

    AccountStorage accountStorage = new AccountStorage();

    @FXML
    public void initialize() {
        List<Account> userAccounts = null;
        try {
            userAccounts = accountStorage.getUserAccounts(App.currentUser.getId());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get user accounts.");
            return;
        }

        // Set properties on scrollPane
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.minViewportHeightProperty().setValue(200);
        scrollPane.maxHeightProperty().setValue(200);

        // Container for which all existing accounts will be appended to.
        // Then we set the content of the ScrollPane to be this VBox.
        VBox parentContainer = new VBox();

        // Dynamically populate Account overview
        for (Account account : userAccounts) {
            // Outer Horizontal Container
            HBox horizontalContainer = new HBox();
            horizontalContainer.getStyleClass().add("margin-bottom-15");

            // Inside Horizontal Container
            ImageView accountIcon = new ImageView();
            accountIcon.setFitHeight(66);
            accountIcon.setFitWidth(66);
            accountIcon.setImage(new Image(String.valueOf(App.class.getResource("images/logo.png"))));

            // Inside Horizontal Container
            VBox verticalContainer = new VBox();
            verticalContainer.prefWidth(176);

            // Inside Vertical Container
            Label accountName = new Label();
            accountName.setPrefWidth(176);
            accountName.setPrefHeight(33);
            accountName.setAlignment(Pos.CENTER);

            Label accountNumber = new Label();
            accountNumber.setPrefWidth(176);
            accountNumber.setPrefHeight(33);
            accountNumber.setAlignment(Pos.CENTER);

            // Inside Horizontal Container
            Label balance = new Label();
            balance.setPrefWidth(133);
            balance.setPrefHeight(65);
            balance.setAlignment(Pos.CENTER);

            // Set values for each accounts
            accountName.setText(account.getName());
            accountNumber.setText(account.getAccountNumber());
            balance.setText(Tools.formatDouble(account.getBalance()));

            // Append JavaFX elements to accountsContainer
            verticalContainer.getChildren().addAll(accountName, accountNumber);
            horizontalContainer.getChildren().addAll(accountIcon, verticalContainer, balance);
            parentContainer.getChildren().add(horizontalContainer);
        }

        scrollPane.setContent(parentContainer);
    }

    public void createAccount(ActionEvent actionEvent) {
        if (AccountValidator.isValidName(accountName.getText().strip(), feedback)) {
            try {
                accountStorage.create(new Account(
                        App.currentUser.getId(),
                        accountName.getText().strip(),
                        Tools.generateAccountNumber(),
                        0)
                );
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to create new account.");
                return;
            }

            try {
                App.setRoot("Accounts");
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to switch view.");
            }
        }
    }

    @FXML
    public void switchToDashboard() {
        try {
            App.setRoot("Dashboard");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

}
