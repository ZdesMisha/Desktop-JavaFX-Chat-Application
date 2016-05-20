package practice.chat.main;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import practice.chat.controller.ChatController;
import practice.chat.controller.LoginController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 12.05.16.
 */
public class SceneDispatcher {

    private Stage stage;
    private ChatController chatController;
    private LoginController loginController;

    SceneDispatcher(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Chat application");
        this.stage.show();
        this.stage.setOnCloseRequest(e -> closeApp());
    }

    public void switchToLogin() {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/loginScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            loginController = loader.getController();
            loginController.setSceneDispatcher(this);
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            closeQuietly(stream);
            ex.printStackTrace();
        }
    }

    public void switchToChat() {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/chatScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            chatController = loader.getController();
            chatController.setSceneDispatcher(this);
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            closeQuietly(stream);
            ex.printStackTrace();
        }
    }

    private void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeApp() {
        Platform.exit();
        if(MainApp.client!=null) {
            MainApp.client.close();
        }

    }

    public void injectLoginLabel(String login) {
        chatController.login.setText(login);
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public ChatController getChatController() {
        return chatController;
    }
}
