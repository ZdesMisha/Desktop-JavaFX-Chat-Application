package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private TextField ipAddressField;
    @FXML
    private TextField portField;

    @FXML
    private Label errorMessage;

    private SceneDispatcher sceneDispatcher;

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    public void handleJoinInButton() {
        String ip = ipAddressField.getText();
        String port = portField.getText();
        String login = loginField.getText(); //TODO validation
        if (isValidLogin(login) && isValidIp(ip) && isValidPort(port)) {
            try {
                sceneDispatcher.switchToChat();
                sceneDispatcher.injectLoginLabel(login);
                MainApp.client = new Client(login, ip, Integer.parseInt(port), sceneDispatcher.getChatController());
                MainApp.client.start();
            } catch (Exception ex) {
                errorMessage.setText("Can not establish connection with server");
                System.out.println("Can not establish connection with server");
                ex.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Login must be from 2 to 10 letters or digits \n" +
                    "Please make sure you typed valid Ip/Port values\n");
            alert.show();
        }
    }

    private boolean isValidLogin(String login) {
        return login.matches("[a-zA-Z0-9]{2,10}");
    }

    private boolean isValidIp(String ip) {
        return ip.matches("127\\.0\\.0\\.1");
    } //TODO IP validation

    private boolean isValidPort(String port) {
        return port.matches("[1-9]{2,4}");
    }
}
