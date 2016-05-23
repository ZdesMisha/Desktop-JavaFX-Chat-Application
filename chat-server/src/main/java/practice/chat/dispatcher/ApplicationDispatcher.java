package practice.chat.dispatcher;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import practice.chat.backend.Server;
import practice.chat.controller.HistoryWindowController;
import practice.chat.controller.ServerController;
import practice.chat.utils.ResourceCloser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by misha on 18.05.16.
 */
public class ApplicationDispatcher {

    public Server server;

    private Stage stage;
    private HistoryWindowController historyWindowController;
    private ServerController serverController;

    public ApplicationDispatcher(Stage stage) {
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
            serverController.setApplicationDispatcher(this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ResourceCloser.closeQuietly(stream);
            ex.printStackTrace();
        }
    }

    public void openHistoryWindow(String roomName) {
        InputStream stream = null;
        try {
            String fxmlFile = "/fxml/historyScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            Stage window = new Stage();
            window.setScene(new Scene(root));
            window.show();
            historyWindowController = loader.getController();
            historyWindowController.setApplicationDispatcher(this);
            historyWindowController.setRoomLable(roomName);
            historyWindowController.showHistory();
        } catch (IOException ex) {
            ResourceCloser.closeQuietly(stream);
            ex.printStackTrace();
        }

    }

    public void closeApp() {
        Platform.exit();
        if(server!=null) {
            server.shutdown();
        }
    }

    public ServerController getServerController() {
        return serverController;
    }

}
