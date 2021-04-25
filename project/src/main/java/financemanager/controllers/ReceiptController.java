package financemanager.controllers;

import financemanager.App;
import financemanager.models.storage.UserStorage;
import financemanager.models.Transaction;
import financemanager.models.User;
import financemanager.utils.Tools;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controls the Receipt view.
 */
public class ReceiptController extends App {

    @FXML
    private Text title;
    @FXML
    private Text transferAmount;
    @FXML
    private Text recipient;
    @FXML
    private Text referenceNumber;
    @FXML
    private ImageView statusImage;
    @FXML
    private Text feedback;

    @FXML
    public void initialize() {
        Transaction transaction = App.lastTransaction;
        UserStorage userStorage = new UserStorage();
        User receiver = null;
        try {
            receiver = userStorage.read(transaction.getReceiverID());
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to retrieve the receiver.");
            return;
        }

        if (transaction.isValid()) {
            statusImage.setImage(new Image(App.class.getResource("images/success.png").toString()));
            title.setText("Payment Successful!");
            transferAmount.setText(Tools.formatDouble(transaction.getAmount()));
            recipient.setText(receiver.getFullName());
            referenceNumber.setText(transaction.getReference());
        }

    }

    @FXML
    public void switchToDashboard() {
        try {
            App.setRoot("Dashboard");
        } catch (IOException e) {
            feedback.setText("An error occurred when trying to switch view.");
        }
    }
}
