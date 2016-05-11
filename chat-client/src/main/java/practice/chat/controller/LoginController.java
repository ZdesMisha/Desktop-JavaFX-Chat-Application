package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import practice.chat.background.Client;

import java.io.IOException;

/**
 * Created by misha on 10.05.16.
 */
public class LoginController {

    private Client client;

    public TextArea textArea;
    public TextField nameField;
    public Button joinInButton;
    public TextField textField = new TextField();
    private Scene scene;

    public void handleJoinInButton() {
        String name = nameField.getText();
        client = new Client(messageTemplate -> {
            Platform.runLater(() -> {
                textArea.appendText(messageTemplate.getMessage() + "\n");
            });
        });
        client.start();
        Stage stage = new Stage();
        String fxmlFile = "/fxml/chatScene.fxml";
        FXMLLoader loader = new FXMLLoader();
        try {
            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            stage.setTitle("Chat");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
