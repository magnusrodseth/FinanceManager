package financemanager.utils;

import financemanager.models.storage.AccountStorage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

/**
 * A static class containing utility methods.
 */
public class Tools {
    private static final int ACCOUNT_NUMBER_LENGTH = 11;

    private static final Currency CURRENCY = Currency.getInstance(Locale.US);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Generates a random account number.
     *
     * @return a String representation of the account number.
     */
    public static String generateAccountNumber() {
        List<String> numbers = new ArrayList<>();

        int max = 9;
        int min = 1;

        // Append random integers between min and max to numbers ArrayList
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            Random random = new Random();
            numbers.add(String.valueOf(random.nextInt(max) + min));
        }

        // Add dots on correct indices in account number
        numbers.add(4, ".");
        numbers.add(7, ".");

        // Convert List of String to one String
        StringBuilder string = new StringBuilder();

        for (String letter : numbers)
            string.append(letter);

        if (accountNumberExists(string.toString()))
            throw new IllegalStateException("Account number already exists");

        return string.toString();
    }

    public static boolean accountNumberExists(String string) {
        try {
            return new AccountStorage().getAccountNumbers().contains(string);
        } catch (IOException e) {
            // If the call above throws, it is for another reason inside AccountStorage
            return false;
        }
    }

    /**
     * Generates a random transaction reference.
     *
     * @return a String representation of the transaction reference.
     */
    public static String generateTransactionReference() {
        return UUID.randomUUID().toString();
    }

    public static String formatDouble(double balance) {
        NUMBER_FORMAT.setCurrency(Currency.getInstance(Locale.US));

        return NUMBER_FORMAT.format(balance) + " " + Tools.CURRENCY.getCurrencyCode();
    }
}
