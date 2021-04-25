package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.Account;
import financemanager.utils.Tools;
import financemanager.utils.validators.AccountValidator;
import financemanager.utils.validators.TransactionValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

/**
 * Controls the Deposit view.
 */
public class DepositController extends App {

    public ComboBox<String> receiverComboBox;
    public TextField amount;
    public Text feedback;

    private final ObservableList<String> receiverAccountNumbers = FXCollections.observableArrayList();

    AccountStorage accountStorage = new AccountStorage();

    @FXML
    public void initialize() {
        // This List holds all Accounts that are owned by the current user.
        List<Account> userAccounts = null;
        try {
            userAccounts = accountStorage.getUserAccounts(App.currentUser.getId());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get user accounts.");
            return;
        }

        for (Account account : userAccounts)
            receiverAccountNumbers.add("" + account.getAccountNumber() + " - " + account.getName());

        receiverComboBox.setItems(receiverAccountNumbers);
    }

    @FXML
    public void switchToDashboard() {
        try {
            App.setRoot("Dashboard");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    public void deposit(ActionEvent actionEvent) {
        if (receiverComboBox.getValue() == null) {
            feedback.setText("Please select an account number before depositing.");
            return;
        }

        String receiverAccountNumber = receiverComboBox.getValue().split(" - ")[0];

        // Read Account object from file storage
        Account receiverAccount = null;
        try {
            receiverAccount = accountStorage.read(receiverAccountNumber);
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to read account from storage.");
            return;
        }

        // Amount must be "parsable", valid and reasonable in order to deposit
        if (TransactionValidator.isValidAmount(amount.getText(), feedback)) {
            float parsedAmount = Float.parseFloat(amount.getText());

            if (!AccountValidator.isValidBalance(receiverAccount.getBalance() + parsedAmount)) {
                feedback.setText("Provided amount causes account balance to be less than 0.");
                return;
            }

            try {
                receiverAccount.deposit(parsedAmount);
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to deposit amount.");
                return;
            }

            feedback.setText(new StringBuilder()
                    .append(Tools.formatDouble(Double.parseDouble(amount.getText())))
                    .append(" was deposited into ")
                    .append(receiverAccount.getName()).toString());
        }
    }
}
