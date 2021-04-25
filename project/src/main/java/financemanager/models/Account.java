package financemanager.models;

import financemanager.models.storage.AccountStorage;
import financemanager.utils.validators.AccountValidator;

import java.io.IOException;

/**
 * A model of an Account object.
 */
public class Account {

    private int userId;
    private String name;
    private String accountNumber;
    private double balance;

    private AccountStorage accountStorage = new AccountStorage();

    public Account(int userId, String name, String accountNumber, double balance) {
        if (AccountValidator.isValidUserId(userId))
            this.userId = userId;

        setName(name);
        setAccountNumber(accountNumber);
        setBalance(balance);
    }


    /**
     * Updates an Account instance by removing a given amount from the Account instance
     *
     * @param amount The amount to be withdrawn
     */
    public void withdraw(double amount) throws IOException {
        if (amount < 0)
            throw new IllegalArgumentException("Amount must be greater than or equal to 0.");

        if (!AccountValidator.isValidBalance(getBalance() - amount))
            throw new IllegalArgumentException("Provided amount causes balance to be less than zero.");

        // Update balance
        setBalance(getBalance() - amount);

        // Update Account instance
        accountStorage.update(this);
    }

    /**
     * Updates an Account instance by adding a given amount from the Account instance
     *
     * @param amount The amount to be deposited
     */
    public void deposit(double amount) throws IOException {
        if (amount < 0)
            throw new IllegalArgumentException("Amount must be greater than or equal to 0.");

        if (!AccountValidator.isValidBalance(getBalance() + amount))
            throw new IllegalArgumentException("Sum of balance and amount must greater than or equal to 0.");

        // Update balance
        setBalance(getBalance() + amount);

        // Update Account instance
        accountStorage.update(this);
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (AccountValidator.isValidName(name))
            this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        if (AccountValidator.isValidAccountNumber(accountNumber))
            this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (AccountValidator.isValidBalance(balance))
            this.balance = balance;
    }

    /**
     * @return JSON representation of an Account object.
     */
    @Override
    public String toString() {
        return "{" +
                "\"userId\": " + "\"" + userId + "\"" + "," +
                "\"name\": " + "\"" + name + "\"" + "," +
                "\"accountNumber\": " + "\"" + accountNumber + "\"" + "," +
                "\"balance\": " + "\"" + balance + "\"" +
                "}";
    }
}
