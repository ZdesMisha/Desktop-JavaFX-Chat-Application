package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import practice.chat.background.Client;
import practice.chat.views.MainApp;

/**
 * Created by misha on 10.05.16.
 */
public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private Button joinInButton;
    @FXML
    private Label message;

    private String name;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void handleJoinInButton() throws Exception {
        name = loginField.getText();
        mainApp.showChatScene();
        mainApp.setClient(new Client(name, mainApp.getChatController()));
        mainApp.getClient().start();
    }
}
