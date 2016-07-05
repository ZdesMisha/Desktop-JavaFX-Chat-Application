package practice.chat.main;

import javafx.application.Application;
import javafx.stage.Stage;
import practice.chat.dispatcher.ApplicationDispatcher;


/**
 * Created by misha on 07.05.16.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ApplicationDispatcher applicationDispatcher = new ApplicationDispatcher(stage);
        applicationDispatcher.switchToLogin();
    }
}
