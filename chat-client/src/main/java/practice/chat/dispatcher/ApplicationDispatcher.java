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
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 12.05.16.
 */
public class ApplicationDispatcher {


    private Client client;

    private Stage stage;
    private ChatController chatController;
    private Scene loginScene;
    private Scene chatScene;


    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDispatcher.class);


    public ApplicationDispatcher(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Chat application");
        stage.setResizable(false);
        this.stage.show();
        this.stage.setOnCloseRequest(e -> closeApplication());
        loadChatController();
        loadLoginController();
    }

    public void switchToLogin() {
        Platform.runLater(() -> stage.setScene(loginScene));
    }

    public void switchToChat() {
        stage.setScene(chatScene);
    }

    private void loadChatController() {
        InputStream stream = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream("/fxml/chatScene.fxml");
            Parent root = loader.load(stream);
            chatController = loader.getController();
            chatController.setApplicationDispatcher(this);
            chatScene = new Scene(root);
        } catch (IOException ex) {
            LOG.error("Unable to load resources", ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void loadLoginController() {
        InputStream stream = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream("/fxml/loginScene.fxml");
            Parent root = loader.load(stream);
            LoginController loginController = loader.getController();
            loginController.setApplicationDispatcher(this);
            loginScene = new Scene(root);
        } catch (IOException ex) {
            LOG.error("Unable to load resources", ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public void closeApplication() {
        Platform.exit();
        if (client != null) {
            client.close();
        }
    }

    public Client getClient() { //TODO change to get instance?
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLoginLabel(String login) {
        chatController.setLogin(login);
    }

    public ChatController getChatController() {
        return chatController;
    }
}
