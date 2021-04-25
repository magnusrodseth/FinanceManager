package financemanager.models;


import financemanager.utils.Tools;
import financemanager.utils.validators.TransactionValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    @DisplayName("Transaction test")
    void transactionTest() {
        Assertions.assertTrue(TransactionValidator.isValidTransaction(100, "50"));
    }

    @Test
    @DisplayName("Message test")
    void messageTest() {
        // Transaction message cannot have custom defined illegal characters
        Assertions.assertFalse(TransactionValidator.isValidMessage("This message is not valid@"));

        Assertions.assertTrue(TransactionValidator.isValidMessage("Hello everyone!"));
    }

    @Test
    @DisplayName("Reference test")
    void referenceTest() {
        // References must follow format from UUID (see Tools class)
        Assertions.assertFalse(TransactionValidator.isValidReference("0"));

        Assertions.assertTrue(TransactionValidator.isValidReference("a3beabcb-0e37-403b-b29b-f9382d0f88ba"));

        Assertions.assertTrue(TransactionValidator.isValidReference(Tools.generateTransactionReference()));
    }

    @Test
    @DisplayName("Receiver ID test")
    void receiverIDTest() {
        Assertions.assertFalse(TransactionValidator.isValidReceiverId(104));

        Assertions.assertFalse(TransactionValidator.isValidReceiverId(-1));
    }
}
