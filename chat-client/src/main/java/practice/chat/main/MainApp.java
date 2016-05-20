package practice.chat.main;

import javafx.application.Application;
import javafx.stage.Stage;
import practice.chat.backend.Client;


/**
 * Created by misha on 07.05.16.
 */
public class MainApp extends Application {

    public static Client client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SceneDispatcher sceneDispatcher = new SceneDispatcher(stage);
        sceneDispatcher.switchToLogin();
    }
}
