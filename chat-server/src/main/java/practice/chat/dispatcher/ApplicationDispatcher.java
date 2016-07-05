package practice.chat.dispatcher;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.backend.Server;
import practice.chat.controller.HistoryController;
import practice.chat.controller.ServerController;
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 18.05.16.
 */
public class ApplicationDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDispatcher.class);

    private final Stage stage;

    private Server server;
    private ServerController serverController;
    private Scene serverScene;

    public ApplicationDispatcher(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Chat Server");
        stage.setResizable(false);
        this.stage.show();
        this.stage.setOnCloseRequest(e -> closeApp());
        loadServerFXMLResources();
    }

    public void createServerChatWindow() {
        stage.setScene(serverScene);
    }

    public void openHistoryWindow(String fileName) {
        InputStream stream = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream("/fxml/historyScene.fxml");
            Parent root = loader.load(stream);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setResizable(false);
            HistoryController historyController = loader.getController();
            historyController.setRoomLabel(fileName);
            historyController.showHistory();
        } catch (IOException ex) {
            LOG.error("Unable to load resources", ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void loadServerFXMLResources() {
        InputStream stream = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream("/fxml/serverScene.fxml");
            Parent root = loader.load(stream);
            serverController = loader.getController();
            serverController.setApplicationDispatcher(this);
            serverScene = new Scene(root);
        } catch (IOException ex) {
            LOG.error("Unable to load resources", ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public void closeApp() {
        Platform.exit();
        if (server != null) {
            server.shutdown();
        }
    }

    public ServerController getServerController() {
        return serverController;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
