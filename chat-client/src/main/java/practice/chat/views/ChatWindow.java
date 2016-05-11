package practice.chat.views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Created by misha on 07.05.16.
 */
public class ChatWindow extends Application {


    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/loginScene.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> Platform.exit());
    }



}
