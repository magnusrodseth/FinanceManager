package financemanager.models;

import financemanager.utils.Tools;
import financemanager.utils.validators.AccountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AccountTest {
    @Test
    @DisplayName("Deposit test")
    void depositTest() {
        // Initialize valid account with balance of 0.0
        // Assumes the generating account number works, as this is in ToolsTest
        Account account = new Account(1, "Example Account", Tools.generateAccountNumber(), 0);

        // Deposit 100.0
        try {
            account.deposit(100);
        } catch (IOException e) {
            Assertions.assertThrows(
                    IOException.class,
                    () -> account.deposit(100)
            );
        }

        // Assert that deposit worked correctly
        Assertions.assertEquals(100, account.getBalance());

        // Withdraw valid amount of 0
        // Logically speaking, 0 is a valid amount to withdraw
        // It is rather the frontend that should handle that it doesn't
        // practically make sense to withdraw 0 from an account.
        try {
            account.deposit(0);
        } catch (IOException e) {
            Assertions.assertThrows(
                    IOException.class,
                    () -> account.deposit(0)
            );
        }

        // Assert that balance was unchanged
        Assertions.assertEquals(100, account.getBalance());

        // Assert that you cannot deposit a negative amount
        // Additionally asserts that the sum of balance and amount is greater
        // than or equal to zero
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-200)
        );
    }

    @Test
    @DisplayName("Withdraw test")
    void withdrawTest() {
        // Initialize valid account with balance of 100.0
        // Assumes the generating account number works, as this is in ToolsTest
        Account account = new Account(100, "Another Example", Tools.generateAccountNumber(), 100);

        // Withdraw 100
        try {
            account.withdraw(50);
        } catch (IOException e) {
            Assertions.assertThrows(
                    IOException.class,
                    () -> account.withdraw(50)
            );
        }

        // Assert that the withdrawal worked
        Assertions.assertEquals(50, account.getBalance());

        // Withdraw valid amount of 0
        // Logically speaking, 0 is a valid amount to withdraw
        // It is rather the frontend that should handle that it doesn't
        // practically make sense to withdraw 0 from an account.
        try {
            account.withdraw(0);
        } catch (IOException e) {
            Assertions.assertThrows(
                    IOException.class,
                    () -> account.withdraw(0)
            );
        }

        // Assert that amount was unchanged
        Assertions.assertEquals(50, account.getBalance());

        // Assert that you cannot deposit a negative amount
        // Additionally asserts that balance - amount is greater
        // than or equal to zero
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(-500)
        );
    }

    @Test
    @DisplayName("User id test")
    void userIdTest() {
        // User id must be greater than 0
        Assertions.assertFalse(AccountValidator.isValidUserId(-10));

        // User id must be greater than 0
        Assertions.assertFalse(AccountValidator.isValidUserId(0));

        // Valid user id passes
        Assertions.assertTrue(AccountValidator.isValidUserId(123));
    }

    @Test
    @DisplayName("Name test")
    void nameTest() {
        // Empty name throws
        Assertions.assertFalse(AccountValidator.isValidName(""));

        // Illegal characters in name and length > 25 throws
        Assertions.assertFalse(AccountValidator.isValidName("æææææøøøøøåååååæææææøøøøøååååå"));

        Assertions.assertTrue(AccountValidator.isValidName("''''''''"));

        // Valid name passes
        Assertions.assertTrue(AccountValidator.isValidName("John Doe"));
    }

    @Test
    @DisplayName("Account number test")
    void accountNumberTest() {
        // Account number should have 2 dots [.]
        Assertions.assertFalse(AccountValidator.isValidAccountNumber("34.5"));

        // First part of account number should have 4 digits
        Assertions.assertFalse(AccountValidator.isValidAccountNumber("23.45.67"));

        // Second part of account number should have 2 digits
        Assertions.assertFalse(AccountValidator.isValidAccountNumber("2345.1.56"));

        // Third part of account number should have 5 digits
        Assertions.assertFalse(AccountValidator.isValidAccountNumber("1234.56.567"));

        // Valid account number passes
        Assertions.assertTrue(AccountValidator.isValidAccountNumber(Tools.generateAccountNumber()));
    }

    @Test
    @DisplayName("Balance test")
    void balanceTest() {
        // Balance must be greater than a negative number
        Assertions.assertFalse(AccountValidator.isValidBalance(-200));

        // Valid balance passes
        Assertions.assertTrue(AccountValidator.isValidBalance(0));

        // Valid balance passes
        Assertions.assertTrue(AccountValidator.isValidBalance(1000));

        // Valid balance passes
        Assertions.assertTrue(AccountValidator.isValidBalance(2.56778));
    }
}
