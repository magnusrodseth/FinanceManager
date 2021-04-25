package financemanager.utils.validators;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.Transaction;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Validates properties related to Transaction objects.
 */
public class TransactionValidator {

    private static AccountStorage accountStorage = new AccountStorage();

    public static boolean isOwnTransaction(Transaction transaction) {
        return transaction.getSenderID() == App.currentUser.getId() || transaction.getReceiverID() == App.currentUser.getId();
    }

    public static boolean isValidTimestamp(String timestamp) {
        return timestamp != null;
    }

    public static boolean isValidSenderId(int senderId) {
        try {
            return accountStorage.read(senderId) != null;
        } catch (IOException e) {
            // If call above throws, there is an error in AccountStorage
            return false;
        }
    }

    public static boolean isValidReceiverId(int receiverId) {
        try {
            return accountStorage.read(receiverId) != null;
        } catch (IOException e) {
            // If call above throws, there is an error in AccountStorage
            return false;
        }
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        try {
            return accountStorage.read(accountNumber) != null;
        } catch (IOException e) {
            // If call above throws, there is an error in AccountStorage
            return false;
        }
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public static boolean isValidAmount(String amount) {
        // amount is not a valid number
        try {
            float parsed = Float.parseFloat(amount);
        } catch (NumberFormatException e) {
            return false;
        }

        // amount <= 0
        if (Float.parseFloat(amount) <= 0)
            return false;

        return true;
    }

    public static boolean isValidAmount(String amount, Text fxmlElement) {
        if (amount.equals("")) {
            fxmlElement.setText("Please select an amount before transferring.");
            return false;
        }

        try {
            float parsed = Float.parseFloat(amount);
        } catch (NumberFormatException e) {
            fxmlElement.setText("Amount must be a number.");
            return false;
        }

        if (Float.parseFloat(amount) <= 0) {
            fxmlElement.setText("Transfer amount must be greater than 0");
            return false;
        }

        return true;
    }

    public static boolean isValidMessage(String message) {
        return message.matches("[a-zA-Z ]+[.,&!?]*");
    }

    public static boolean isValidReference(String reference) {
        return reference.length() == 36 && reference.split("-").length == 5;
    }

    public static boolean isEmptyAmount(String amount) {
        return amount.equals("");
    }

    public static boolean isValidTransaction(double senderBalance, String transferAmount) {
        return senderBalance >= Float.parseFloat(transferAmount);
    }
}
