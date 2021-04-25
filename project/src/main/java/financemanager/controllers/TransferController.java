package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.storage.TransactionStorage;
import financemanager.models.Account;
import financemanager.models.Transaction;
import financemanager.utils.Tools;
import financemanager.utils.validators.TransactionValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controls the Transfer view.
 */
public class TransferController extends App {
    public TextField amount;
    public Text feedback;
    public TextField message;
    public Label availableBalance;

    @FXML
    private ComboBox<String> senderComboBox;

    @FXML
    private ComboBox<String> receiverComboBox;

    private final ObservableList<String> senderAccountNumbers = FXCollections.observableArrayList();
    private final ObservableList<String> receiverAccountNumbers = FXCollections.observableArrayList();

    AccountStorage accountStorage = new AccountStorage();
    TransactionStorage transferStorage = new TransactionStorage();

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

        // This List holds all Accounts that are not owned by the current user.
        List<Account> otherAccounts = null;
        try {
            otherAccounts = accountStorage.getOtherAccounts(App.currentUser.getId());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get all other accounts.");
            return;
        }

        for (Account account : userAccounts)
            senderAccountNumbers.add(account.getAccountNumber() + " - " + account.getName());

        for (Account account : otherAccounts)
            receiverAccountNumbers.add(account.getAccountNumber() + " - " + account.getName());

        senderComboBox.setItems(senderAccountNumbers);
        receiverComboBox.setItems(receiverAccountNumbers);
    }

    @FXML
    public void updateAvailableBalance() {
        String accountNumber = senderComboBox.getValue().split(" - ")[0];

        double balance;
        try {
            balance = accountStorage.read(accountNumber).getBalance();
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get account balance.");
            return;
        }

        var formattedBalance = Tools.formatDouble(balance);

        availableBalance.setText("Available balance: " + formattedBalance);
    }

    @FXML
    public void switchToReceipt() {
        try {
            App.setRoot("Receipt");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
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

    public void transfer(ActionEvent actionEvent) {
        if (senderComboBox.getValue() == null || receiverComboBox.getValue() == null) {
            feedback.setText("Please select sender and receiver before transferring an amount");
            return;
        }

        // Get accounts number based on String values in ComboBox
        String senderAccountNumber = senderComboBox.getValue().split(" - ")[0];
        String receiverAccountNumber = receiverComboBox.getValue().split(" - ")[0];

        Account sender = null;
        Account receiver = null;
        try {
            sender = accountStorage.read(senderAccountNumber);
            receiver = accountStorage.read(receiverAccountNumber);
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to read accounts.");
            return;
        }

        if (isValidTransfer(sender, receiver)) {
            String formattedDate = DateTimeFormatter
                    .ofPattern("dd-MM-yyyy HH:mm")
                    .format(LocalDateTime.now());

            Transaction transaction = new Transaction(
                    formattedDate, sender.getUserId(), receiver.getUserId(),
                    sender.getAccountNumber(), receiver.getAccountNumber(),
                    Float.parseFloat(amount.getText()), true, message.getText(),
                    Tools.generateTransactionReference()
            );

            try {
                transferStorage.create(transaction);
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to fulfill the transaction.");
                return;
            }

            try {
                sender.withdraw(Float.parseFloat(amount.getText()));
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to withdraw sender's amount.");
                return;
            }

            try {
                receiver.deposit(Float.parseFloat(amount.getText()));
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to deposit receiver's amount.");
                return;
            }

            App.lastTransaction = transaction;

            switchToReceipt();
        }
    }

    private boolean isValidTransfer(Account sender, Account receiver) {
        if (TransactionValidator.isEmptyAmount(amount.getText())) {
            feedback.setText("Please select an amount before transferring. Please try again.");
            return false;
        }

        if (!TransactionValidator.isValidAmount(amount.getText())) {
            feedback.setText("Amount must be a number and greater than 0. Please try again.");
            return false;
        }

        if (!TransactionValidator.isValidAccountNumber(sender.getAccountNumber())) {
            feedback.setText("Sender's account number does not exist. Please try again.");
            return false;
        }

        if (!TransactionValidator.isValidAccountNumber(receiver.getAccountNumber())) {
            feedback.setText("Receiver's account number does not exist. Please try again.");
            return false;
        }

        if (!TransactionValidator.isValidTransaction(sender.getBalance(), amount.getText())) {
            feedback.setText("Transfer amount cannot be greater than sender's balance. Please try again.");
            return false;
        }

        return true;
    }
}
