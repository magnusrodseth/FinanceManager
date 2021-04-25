package financemanager.utils.validators;

import javafx.scene.text.Text;

/**
 * Validates properties related to Account objects.
 */
public class AccountValidator {
    public static boolean isValidUserId(int userId) {
        return userId > 0;
    }

    public static boolean isValidName(String name) {
        if (name.equals(""))
            return false;

        if (!name.matches("^[a-zA-Z ']{1,25}"))
            return false;

        return true;
    }

    public static boolean isValidName(String accountName, Text fxmlElement) {
        if (accountName.equals("")) {
            fxmlElement.setText("Account name cannot be empty. Please try again.");
            return false;
        }
        if (!accountName.matches("^[a-zA-Z ']{1,25}")) {
            fxmlElement.setText("Account name must only have letters 'A' through 'Z' and max. 25 characters. Please try again.");
            return false;
        }

        return true;
    }


    public static boolean isValidAccountNumber(String accountNumber) {
        String[] splitAccountNumber = accountNumber.split("\\.");

        // Account number should have 2 dots [.]
        if (splitAccountNumber.length != 3)
            return false;

        // First part of account number should have 4 digits
        if (splitAccountNumber[0].length() != 4)
            return false;

        // Second part of account number should have 2 digits
        if (splitAccountNumber[1].length() != 2)
            return false;

        // Third part of account number should have 5 digits
        if (splitAccountNumber[2].length() != 5)
            return false;

        return true;
    }

    public static boolean isValidBalance(double amount) {
        return amount >= 0;
    }
}
