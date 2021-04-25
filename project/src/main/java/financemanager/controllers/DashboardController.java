package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.AccountStorage;
import financemanager.models.storage.TransactionStorage;
import financemanager.models.storage.UserStorage;
import financemanager.models.Account;
import financemanager.models.Transaction;
import financemanager.models.User;
import financemanager.utils.Tools;
import financemanager.utils.validators.TransactionValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controls the Dashboard view.
 */
public class DashboardController extends App {
    public Label name;
    public Label balance;
    public PieChart piechart;

    public ScrollPane transactionPane;

    private final AccountStorage accountStorage = new AccountStorage();
    private final UserStorage userStorage = new UserStorage();
    private final TransactionStorage transactionStorage = new TransactionStorage();

    private final ObservableList<PieChart.Data> accountsData = FXCollections.observableArrayList();
    public Text feedback;


    @FXML
    public void switchToTransfer(ActionEvent actionEvent) {
        try {
            App.setRoot("Transfer");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    @FXML
    public void switchToAccounts(ActionEvent actionEvent) {
        try {
            App.setRoot("Accounts");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }

    @FXML
    public void switchToLogin(ActionEvent actionEvent) {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }


    public void initialize() {
        // Calculate the total user balance
        double userBalance = 0;
        try {
            userBalance = accountStorage.getUserBalance(App.currentUser.getId());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get user balance.");
            return;
        }

        List<Transaction> transactions = null;
        try {
            transactions = transactionStorage.getTransactions();
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get transactions.");
            return;
        }

        name.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        balance.setText("Total balance: " + Tools.formatDouble(userBalance));

        VBox parentContainer = new VBox();

        // Get most recent transactions first
        Collections.reverse(transactions);

        for (Transaction transaction : transactions) {

            // Checks if transaction receiver or sender is yourself, if not skip current iteration
            if (!TransactionValidator.isOwnTransaction(transaction)) {
                continue;
            }

            User sender = null;
            try {
                sender = userStorage.read(transaction.getSenderID());
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to retrieve an account.");
                return;
            }

            User receiver = null;
            try {
                receiver = userStorage.read(transaction.getReceiverID());
            } catch (IOException e) {
                feedback.setText("An error occurred when trying to retrieve an account.");
                return;
            }

            // Outer Horizontal Container
            HBox horizontalContainer = new HBox();
            horizontalContainer.getStyleClass().add("margin-bottom-15");

            // Inside Horizontal Container
            ImageView transactionIcon = new ImageView();
            transactionIcon.setFitHeight(66);
            transactionIcon.setFitWidth(66);
            transactionIcon.setImage(new Image(String.valueOf(App.class.getResource("images/logo.png"))));

            // Inside Horizontal Container
            VBox verticalContainer = new VBox();
            verticalContainer.prefWidth(176);

            // Inside Vertical Container
            Label fromUserFullName = new Label();
            fromUserFullName.setPrefWidth(176);
            fromUserFullName.setPrefHeight(22);
            fromUserFullName.setAlignment(Pos.CENTER);

            // Inside Vertical Container
            Label toUserFullName = new Label();
            toUserFullName.setPrefWidth(176);
            toUserFullName.setPrefHeight(22);
            toUserFullName.setAlignment(Pos.CENTER);

            Label transactionDate = new Label();
            transactionDate.setPrefWidth(176);
            transactionDate.setPrefHeight(22);
            transactionDate.setAlignment(Pos.CENTER);

            // Inside Horizontal Container
            Label amount = new Label();
            amount.setPrefWidth(133);
            amount.setPrefHeight(65);
            amount.setAlignment(Pos.CENTER);

            // Set values for each accounts
            String senderText = userIsSelf(sender.getId())
                    ? "Me"
                    : sender.getFullName();

            String receiverText = userIsSelf(receiver.getId())
                    ? "You"
                    : receiver.getFullName();

            fromUserFullName.setText("From: " + senderText);
            toUserFullName.setText("To: " + receiverText);

            transactionDate.setText(transaction.getTransactionTimestamp());
            amount.setText(Tools.formatDouble(transaction.getAmount()));

            // Append JavaFX elements to accountsContainer
            verticalContainer.getChildren().addAll(fromUserFullName, toUserFullName, transactionDate);
            horizontalContainer.getChildren().addAll(transactionIcon, verticalContainer, amount);
            parentContainer.getChildren().add(horizontalContainer);
        }

        transactionPane.setContent(parentContainer);

        // We need to call these methods sequentially in order for elements to load.
        // Custom colors and tooltips cannot be rendered before PieChart is drawn.
        drawPieChart();

        // Set PieChart colors
        applyCustomColors(accountsData);

        // Add tooltips to PieChart
        addTooltips(piechart);
    }

    private void drawPieChart() {
        // This List holds all Accounts that are owned by the current user.
        List<Account> userAccounts = null;
        try {
            userAccounts = accountStorage.getUserAccounts(App.currentUser.getId());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to get user accounts.");
            return;
        }

        for (Account account : userAccounts) {
            accountsData.add(new PieChart.Data(account.getName(), account.getBalance()));
        }

        // Calculate the total user balance
        double userBalance = Collections.max(
                userAccounts.stream().map(Account::getBalance).collect(Collectors.toList())
        );

        // Hide PieChart component if user's total balance is 0
        if (userBalance == 0)
            piechart.setVisible(false);

        // Set content of PieChart component to be user accounts
        piechart.setData(accountsData);
        piechart.setLegendSide(Side.RIGHT);
        piechart.setLabelsVisible(false);
        piechart.setLegendVisible(false);
    }

    // Source: https://stackoverflow.com/questions/28853507/display-tooltip-on-piechart
    private void addTooltips(PieChart piechart) {
        piechart.getData().forEach(data -> {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                // Create tooltip object
                Tooltip tooltip = new Tooltip();
                tooltip.setShowDelay(Duration.millis(0));
                tooltip.setText("$" + data.getPieValue() + " - " + data.getName());
                Tooltip.install(data.getNode(), tooltip);
            });
        });
    }

    private void applyCustomColors(ObservableList<PieChart.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            final double RATIO = 7;

            String rgba = new StringBuilder()
                    .append("rgba(")
                    .append(158) // ------------------------- Red
                    .append(",")
                    .append(197 + (i * RATIO * 2)) // --- Green
                    .append(",")
                    .append(171 + (i * RATIO * 4)) // --- Blue
                    .append(",1)").toString();

            data.get(i).getNode().setStyle(new StringBuilder()
                    .append("-fx-pie-color: ")
                    .append(rgba).toString());
        }
    }


    public boolean userIsSelf(int identifier) {
        return identifier == App.currentUser.getId();
    }

    public void switchToDeposit(ActionEvent actionEvent) {
        try {
            App.setRoot("Deposit");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }
}
