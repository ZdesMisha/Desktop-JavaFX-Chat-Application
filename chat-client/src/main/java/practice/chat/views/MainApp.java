package practice.chat.views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import practice.chat.background.Client;
import practice.chat.controller.ChatController;
import practice.chat.controller.LoginController;

import java.io.IOException;


/**
 * Created by misha on 07.05.16.
 */
public class MainApp extends Application {

    private Stage stage;
    private Client client;
    private LoginController loginController;
    private ChatController chatController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Chat Application");
        showLoginScene();
    }

    public void showLoginScene() {
        try {
            String fxmlFile = "/fxml/loginScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            loginController = loader.getController();
            loginController.setMainApp(this);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showChatScene() throws IOException {
        try {
            String fxmlFile = "/fxml/chatScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
            chatController = loader.getController();
            chatController.setMainApp(this);
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
