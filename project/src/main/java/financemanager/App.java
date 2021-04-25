package financemanager;

import financemanager.models.Transaction;
import financemanager.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    // These static fields keep track of the global variables.
    public static User currentUser;
    public static Transaction lastTransaction;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login"));

        // Set global stylesheet
        App.setStyles("main");

        int SCENE_WIDTH = 600;
        int SCENE_HEIGHT = 850;

        // Set the stage
        stage.setMinWidth(SCENE_WIDTH);
        stage.setMaxWidth(SCENE_WIDTH);
        stage.setMinHeight(SCENE_HEIGHT);
        stage.setMaxHeight(SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();

    }

    public static void setStyles(String css) {
        scene.getStylesheets().add(App.class.getResource("styles/" + css + ".css").toString());
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = setFXMLLoader(fxml);
        return fxmlLoader.load();
    }

    public static FXMLLoader setFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
    }

    public static void main(String[] args) {
        launch(App.class, args);
    }
}