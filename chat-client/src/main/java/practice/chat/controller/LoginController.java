package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import practice.chat.background.Client;
import practice.chat.main.MainApp;
import practice.chat.main.SceneDispatcher;

/**
 * Created by misha on 10.05.16.
 */
public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private Label message;

    private SceneDispatcher sceneDispatcher;

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    public void handleJoinInButton() {
        String login = loginField.getText();
        try {
            sceneDispatcher.switchToChat();
            sceneDispatcher.injectLoginLabel(login);
            MainApp.client = new Client(login, sceneDispatcher.getChatController());
            MainApp.client.start();
        } catch (Exception ex) {
            message.setText("Can not establish connection with server");
            System.out.println("Can not establish connection with server");
            ex.printStackTrace();
        }
    }
}
