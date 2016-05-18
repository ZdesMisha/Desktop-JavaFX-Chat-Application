package practice.chat.main;

import javafx.application.Application;
import javafx.stage.Stage;
import practice.chat.server.Server;


/**
 * Created by misha on 07.05.16.
 */
public class ServerApp extends Application {

    public static Server server;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SceneDispatcher sceneDispatcher = new SceneDispatcher(stage);
        sceneDispatcher.createServerChatWindow();
    }


}
