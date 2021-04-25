package financemanager.models;

import financemanager.utils.validators.TransactionValidator;


public class Transaction {

    private String transactionTimestamp;
    private int senderID;
    private int receiverID;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private double amount;
    private final boolean isValid;
    private String message;
    private String reference;

    public Transaction(
            String transactionTimestamp,
            int senderID,
            int receiverID,
            String senderAccountNumber,
            String receiverAccountNumber,
            double amount,
            boolean isValid,
            String message,
            String reference
    ) {
        setTransactionTimestamp(transactionTimestamp);
        setSenderID(senderID);
        setReceiverID(receiverID);
        setSenderAccountNumber(senderAccountNumber);
        setReceiverAccountNumber(receiverAccountNumber);
        setAmount(amount);

        this.isValid = isValid;

        setMessage(message);
        setReference(reference);
    }

    public String getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(String transactionTimestamp) {
        if (TransactionValidator.isValidTimestamp(transactionTimestamp))
            this.transactionTimestamp = transactionTimestamp;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        if (TransactionValidator.isValidSenderId(senderID))
            this.senderID = senderID;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        if (TransactionValidator.isValidReceiverId(receiverID))
            this.receiverID = receiverID;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        if (TransactionValidator.isValidAccountNumber(senderAccountNumber))
            this.senderAccountNumber = senderAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        if (TransactionValidator.isValidAccountNumber(receiverAccountNumber))
            this.receiverAccountNumber = receiverAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (TransactionValidator.isValidAmount(amount))
            this.amount = amount;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setMessage(String message) {
        if (TransactionValidator.isValidMessage(message))
            this.message = message;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        if (TransactionValidator.isValidReference(reference))
            this.reference = reference;
    }

    /**
     * @return JSON representation of a Transaction object.
     */
    @Override
    public String toString() {
        return "{" +
                "\"transactionTimestamp\": " + "\"" + transactionTimestamp + "\"" + "," +
                "\"senderID\": " + "\"" + senderID + "\"" + "," +
                "\"receiverID\": " + "\"" + receiverID + "\"" + "," +
                "\"senderAccountNumber\": " + "\"" + senderAccountNumber + "\"" + "," +
                "\"receiverAccountNumber\": " + "\"" + receiverAccountNumber + "\"" + "," +
                "\"amount\": " + "\"" + amount + "\"" + "," +
                "\"isValid\": " + "\"" + isValid + "\"" + "," +
                "\"message\": " + "\"" + message + "\"" + "," +
                "\"reference\": " + "\"" + reference + "\"" +
                "}";
    }
}
