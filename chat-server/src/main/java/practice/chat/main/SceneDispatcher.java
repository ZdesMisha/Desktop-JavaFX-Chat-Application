package practice.chat.main;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import practice.chat.controller.HistoryWindowController;
import practice.chat.controller.ServerController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 18.05.16.
 */
public class SceneDispatcher {

    private Stage stage;
    private HistoryWindowController historyWindowController;
    private ServerController serverController;

    SceneDispatcher(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Chat Server");
        this.stage.show();
        this.stage.setOnCloseRequest(e -> closeApp());
    }

    public void createServerChatWindow() {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/serverScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            serverController = loader.getController();
            serverController.setSceneDispatcher(this);
            stage.setScene(new Scene(root));
            this.stage.setTitle("Chat Server");
            this.stage.show();
        } catch (IOException ex) {
            closeQuietly(stream);
            ex.printStackTrace();
        }
    }

    public void openHistoryWindow() {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/historyScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            historyWindowController = loader.getController();
            historyWindowController.setSceneDispatcher(this);
            Stage window = new Stage();
            window.setScene(new Scene(root));
            window.show();
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
        ServerApp.server.shutdown();
        Platform.exit();
    }

    public ServerController getServerController() {
        return serverController;
    }

    public String getRoomName() {
        return serverController.roomChoice.getValue();
    }
}
