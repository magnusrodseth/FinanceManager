package financemanager.tools;

import financemanager.models.storage.AccountStorage;
import financemanager.models.Account;
import financemanager.utils.Tools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ToolsTest {
    @Test
    @DisplayName("Account number test")
    void accountNumberTest() {
        Account account = new Account(20, "test-name", Tools.generateAccountNumber(), 210);

        String[] splitAccountNumber = account.getAccountNumber().split("\\.");

        // Account number should have 2 dots [.]
        Assertions.assertEquals(3, splitAccountNumber.length);

        // First part of account number should have 4 digits
        Assertions.assertEquals(4, splitAccountNumber[0].length());

        // Second part of account number should have 2 digits
        Assertions.assertEquals(2, splitAccountNumber[1].length());

        // Third part of account number should have 5 digits
        Assertions.assertEquals(5, splitAccountNumber[2].length());

        // Initialize AccountStorageController
        AccountStorage accountStorage = new AccountStorage();
        try {
            accountStorage.create(account);
        } catch (IOException e) {
            Assertions.assertThrows(IOException.class, () -> accountStorage.create(account));
        }

        // Existing accountNumber returns true
        Assertions.assertTrue(Tools.accountNumberExists(account.getAccountNumber()));

        // Cleanup and asserts that delete method throws when IOException occurs
        try {
            accountStorage.delete(account.getAccountNumber());
        } catch (IOException e) {
            Assertions.assertThrows(
                    IOException.class,
                    () -> accountStorage.delete(account.getAccountNumber())
            );
        }

    }

}
