package practice.chat.main;

import javafx.application.Application;
import javafx.stage.Stage;
import practice.chat.backend.Server;
import practice.chat.controller.SceneDispatcher;


/**
 * Created by misha on 07.05.16.
 */
public class ServerApp extends Application {

    public static Server server; //TODO is it have to be thread safe?


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SceneDispatcher sceneDispatcher = new SceneDispatcher(stage);
        Runtime.getRuntime().addShutdownHook(new Thread(sceneDispatcher::closeApp)); //TODO may be System.exit?
        sceneDispatcher.createServerChatWindow();
    }


}
