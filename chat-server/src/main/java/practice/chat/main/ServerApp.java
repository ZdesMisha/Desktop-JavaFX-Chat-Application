package practice.chat.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import practice.chat.controller.ServerController;
import practice.chat.server.Server;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by misha on 07.05.16.
 */
public class ServerApp extends Application {

    private Stage stage;
    public static ServerController serverController;
    public static Server server; //TODO may be it sholbe be better to use getInstance?


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        InputStream stream = null;
        this.stage = stage;
        try {
            String fxmlFile = "/fxml/serverScene.fxml";
            FXMLLoader loader = new FXMLLoader();
            stream = getClass().getResourceAsStream(fxmlFile);
            Parent root = loader.load(stream);
            serverController = loader.getController();
            stage.setScene(new Scene(root));
            this.stage.setTitle("Chat Server");
            this.stage.show();
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

}
