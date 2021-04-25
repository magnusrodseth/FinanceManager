package financemanager.models.storage;


import financemanager.models.Transaction;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls persistent storage of Transaction objects.
 */
public class TransactionStorage extends Storage<Transaction> {

    public TransactionStorage() {
        super("transactions.json");
    }

    @Override
    public void create(Transaction instance) throws IOException {
        super.create(instance);
    }

    @Override
    public Transaction read(String identifier) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try {
            transactions = getTransactions();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all transactions.");
        }

        for (Transaction transaction : transactions) {
            if (transaction.getReference().equals(identifier))
                return transaction;
        }
        return null;
    }

    @Override
    public void update(String identifier, Transaction instance) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try {
            transactions = getTransactions();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all transactions.");
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction item = transactions.get(i);
            if (item.getReference().equals(identifier)) {
                transactions.set(i, instance);
                break;
            }
        }

        try {
            writeToJson(transactions.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write transaction to file.");
        }
    }

    @Override
    public void delete(String identifier) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try {
            transactions = getTransactions();
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to get all transactions.");
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction item = transactions.get(i);
            if (item.getReference().equals(identifier)) {
                transactions.remove(i);
                break;
            }
        }

        try {
            writeToJson(transactions.toString());
        } catch (IOException e) {
            throw new IOException("An error occurred when trying to write all transactions to file.");
        }
    }


    public List<Transaction> getTransactions() throws IOException {
        List<Transaction> allTransactions = new ArrayList<>();

        String fileContent = super.getFileContent();

        // Matches all instances of JSON objects in fileContent

        Pattern curlyPattern = Pattern.compile("\\{([^}]+)\\}", Pattern.DOTALL);
        Matcher curlyMatcher = curlyPattern.matcher(fileContent);

        List<String> objects = new ArrayList<>();

        while (curlyMatcher.find()) {
            objects.add(curlyMatcher.group(1));
        }

        // Regex that matches JSON keys to values, explicitly string values
        Pattern pattern = Pattern.compile("\"*\"\\s*:\\s*\"(.+?)\"", Pattern.DOTALL);

        // The List values holds instances of the List objects
        List<List<String>> values = new ArrayList<>();

        // Matches values in array with JSON objects (List<String> objects)
        // and groups them inside their own ArrayList within the List values
        for (var i = 0; i < objects.size(); i++) {
            Matcher matcher = pattern.matcher(objects.get(i));

            values.add(new ArrayList<>());

            while (matcher.find()) {
                values.get(i).add(matcher.group(1));
            }

        }

        // Split file content into each JSON object
        // If there exists only one JSON object,
        // the split still works
        for (var i = 0; i < values.size(); i++) {
            Transaction transaction = constructTransaction(values.get(i));
            allTransactions.add(transaction);
        }

        return allTransactions;
    }

    /**
     * Constructs a Transaction object based on provided String data.
     *
     * @param data A List< String > representation of a Transaction object
     * @return An instance of the Transaction class
     * @throws IllegalStateException If an error occurs when constructing a Transaction
     */
    private Transaction constructTransaction(List<String> data) throws IllegalStateException {

        Transaction transaction = null;

        // This list will always only contain one constructor, as each model has only one constructor

        Constructor[] constructors = Transaction.class.getConstructors();


        for (Constructor constructor : constructors) {
            // To instantiate a new instance of the class, all the values have to correlate with
            // the constructor of the class
            try {
                transaction = (Transaction) constructor.newInstance(data.get(0), Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), data.get(3), data.get(4), Double.parseDouble(data.get(5)), Boolean.parseBoolean(data.get(6)), data.get(7), data.get(8));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("An error occurred when trying to construct a new Transaction instance.");
            }
        }

        return transaction;
    }
}
