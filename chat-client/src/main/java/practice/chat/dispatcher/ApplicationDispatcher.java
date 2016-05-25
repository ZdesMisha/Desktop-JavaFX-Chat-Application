package practice.chat.dispatcher;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.backend.Client;
import practice.chat.controller.ChatController;
import practice.chat.controller.LoginController;
import practice.chat.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 12.05.16.
 */
public class ApplicationDispatcher {

    public Client client;

    private Stage stage;
    private ChatController chatController;
    private LoginController loginController;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDispatcher.class);


    public ApplicationDispatcher(Stage stage) { //TODO how to move on center
        this.stage = stage;
        this.stage.setTitle("Chat application");
        this.stage.show();
        this.stage.setOnCloseRequest(e -> closeApplication());
    }

    public void switchToLogin() {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/loginScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            loginController = loader.getController();
            loginController.setApplicationDispatcher(this);
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            LOG.error("Failure during switching to Login scene");
            LOG.error("Error stack:\n" + ex);
            IOUtils.closeQuietly(stream);
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
            chatController.setApplicationDispatcher(this);
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            LOG.error("Failure during switching to Chat scene");
            LOG.error("Error stack:\n" + ex);
            IOUtils.closeQuietly(stream);
        }
    }


    public void closeApplication() {
        Platform.exit();
        if (client != null) {
            client.close();
        }
    }

    public void injectLoginLabel(String login) {
        chatController.login.setText(login);
    }

    public ChatController getChatController() {
        return chatController;
    }

    public LoginController getLoginController() {
        return loginController;
    }
}
