package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import practice.chat.protocol.shared.message.MessageTemplate;
import practice.chat.views.MainApp;

import java.io.IOException;

/**
 * Created by misha on 06.05.16.
 */
public class ChatController {

    @FXML
    private TextArea chatDisplay;
    @FXML
    private Button sendButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField field;

    private String message;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    public void handleLogoutButton() {
        mainApp.getClient().close();
        mainApp.showLoginScene();
    }

    public void handleSendButton() {
        try {
            message = field.getText();
            mainApp.getClient().sendTextMessage(message);
            field.clear();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void displayMessage(MessageTemplate messageTemplate) {
        chatDisplay.appendText(messageTemplate.getMessage() + "\n");
    }

    public void handleBrokenConnection(){
        mainApp.showLoginScene();
    }

}
