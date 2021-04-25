package financemanager.models.storage;

import financemanager.models.Account;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Controls persistent storage of Account objects.
 */
public class AccountStorage extends Storage<Account> {

    public AccountStorage() {
        super("accounts.json");
    }

    @Override
    public void create(Account instance) throws IOException {
        super.create(instance);
    }

    @Override
    public Account read(String identifier) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        if (allAccounts == null)
            throw new NullPointerException("Could not find any accounts.");

        for (Account account : allAccounts) {
            if (account.getAccountNumber().equals(identifier))
                return account;
        }
        return null;
    }

    /**
     * Reads the first Account associates with a user ID.
     *
     * @param userId An int representation of an ID belonging to a user.
     * @return an Account instance.
     * @throws IOException if no Account belongs to user ID
     */
    public Account read(int userId) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        for (Account account : allAccounts) {
            if (account.getUserId() == userId)
                return account;
        }
        return null;
    }

    public void update(Account instance) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        for (int i = 0; i < allAccounts.size(); i++) {
            Account item = allAccounts.get(i);

            if (item.getAccountNumber().equals(instance.getAccountNumber())) {
                allAccounts.set(i, instance);
                break;
            }
        }

        try {
            writeToJson(allAccounts.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write to JSON.");
        }
    }

    @Override
    public void update(String identifier, Account instance) {
    }

    @Override
    public void delete(String identifier) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        for (int i = 0; i < allAccounts.size(); i++) {
            Account item = allAccounts.get(i);
            if (item.getAccountNumber().equals(identifier)) {
                allAccounts.remove(i);
                break;
            }
        }

        try {
            writeToJson(allAccounts.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write to JSON.");
        }
    }

    public List<Account> getAllAccounts() throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        String fileContent = super.getFileContent();

        // Matches all instances of JSON objects in fileContent
        Pattern curlyPattern = Pattern.compile("\\{([^}]+)\\}", Pattern.DOTALL);
        Matcher curlyMatcher = curlyPattern.matcher(fileContent);


        List<String> objects = new ArrayList<>();

        while (curlyMatcher.find())
            objects.add(curlyMatcher.group(1));

        // Regex that matches JSON keys to values, explicitly string values
        Pattern pattern = Pattern.compile("\"*\"\\s*:\\s*\"(.+?)\"", Pattern.DOTALL);

        // The List values holds instances of the List objects
        List<List<String>> values = new ArrayList<>();

        // Matches values in array with JSON objects (List<String> objects)
        // and groups them inside their own ArrayList within the List values
        for (var i = 0; i < objects.size(); i++) {
            // Match key-value pairs in each JSON object
            Matcher matcher = pattern.matcher(objects.get(i));

            // For each JSON object, add an empty ArrayList
            // This is used to group key-value pairs to the corresponding JSON object
            values.add(new ArrayList<>());

            while (matcher.find())
                values.get(i).add(matcher.group(1));
        }

        // Split file content into each JSON object
        // If there exists only one JSON object,
        // the split still works
        for (int i = 0; i < values.size(); i++) {
            Account account = constructAccount(values.get(i));
            if (account != null)
                allAccounts.add(account);
        }

        return allAccounts;
    }

    /**
     * Constructs an Account object based on provided String data.
     *
     * @param data A List< String > representation of an Account object
     * @return An instance of the Account class
     * @throws IllegalStateException If an error occurs when constructing an Account
     */
    private Account constructAccount(List<String> data) throws IllegalStateException {

        Account account = null;

        // This list will always only contain one constructor, as each model has only one constructor
        Constructor[] constructors = Account.class.getConstructors();

        for (Constructor constructor : constructors) {
            // To instantiate a new instance of the class, all the values have to correlate with
            // the constructor of the class
            try {
                account = (Account) constructor.newInstance(
                        Integer.parseInt(data.get(0)),
                        data.get(1), data.get(2),
                        Double.parseDouble(data.get(3))
                );
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("An error occurred when trying to construct a new Account instance.");
            }
        }

        return account;
    }

    public List<Account> getUserAccounts(int userId) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        List<Account> userAccounts = new ArrayList<>();

        for (Account account : allAccounts) {
            if (account.getUserId() == userId)
                userAccounts.add(account);
        }

        return userAccounts;
    }

    public List<Account> getOtherAccounts(int userId) throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        List<Account> userAccounts = getUserAccounts(userId);

        // This List holds all account numbers owned by the current user.
        List<String> userAccountNumbers = userAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toList());

        List<Account> otherAccounts = new ArrayList<>();

        for (Account account : allAccounts) {
            if (!userAccountNumbers.contains(account.getAccountNumber()))
                otherAccounts.add(account);
        }

        return otherAccounts;
    }

    public double getUserBalance(int id) throws IOException {
        double userBalance = 0;

        List<Double> userAccounts = getUserAccounts(id).stream().map(Account::getBalance).collect(Collectors.toList());

        // Iterate through list of user account balance
        for (double amount : userAccounts)
            userBalance += amount;

        return userBalance;
    }

    public List<String> getAccountNumbers() throws IOException {
        List<Account> allAccounts = new ArrayList<>();

        try {
            allAccounts = getAllAccounts();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all accounts.");
        }

        return allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toList());
    }
}
