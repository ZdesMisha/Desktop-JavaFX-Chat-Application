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
import practice.chat.protocol.shared.message.TextMessage;

import java.io.IOException;

/**
 * Created by misha on 06.05.16.
 */
public class ChatController {


    private Client client;

    public TextArea textArea;
    public TextField nameField;
    public Button sendButton;
    public Button logoutButton;
    public Button joinInButton;
    public TextField textField = new TextField();
    private String message;
    private String name;


    public void handleLogoutButton() {
    }



    public void handleSendButton() {
        try {
            message = textField.getText();
            client.sendMessage(message);
            textField.clear();
            //sendButton.setDisable(true);
        } catch (IOException ex) {
            textArea.appendText("No connection to server! \n");
            ex.printStackTrace();
        }

    }
}
