package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import practice.chat.history.HistoryManager;
import practice.chat.main.SceneDispatcher;
import practice.chat.main.ServerApp;
import practice.chat.server.Server;

import java.util.ArrayList;

/**
 * Created by misha on 17.05.16.
 */
public class ServerController {

    private HistoryManager historyManager = HistoryManager.getInstance();

    @FXML
    private TextArea serverLog;

    @FXML
    private Button runButton;

    @FXML
    private Button shutdownButton;

    public ChoiceBox<String> roomChoice;

    private SceneDispatcher sceneDispatcher;

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }


    public void handleRunButton() {
        displayMessage("Starting server...");
        ServerApp.server = new Server(sceneDispatcher.getServerController());
        ServerApp.server.start();
        updateRoomList(historyManager.getHistoryFileNames());
        runButton.setDisable(true);
        shutdownButton.setDisable(false);

    }

    public void handleUpdateHistoryListButton(){
        updateRoomList(historyManager.getHistoryFileNames());
    }

    public void handleShutdownButton() {
        // Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { //TODO implement it somethere...
        //    public void run() {
        displayMessage("Shutdown server...");
        ServerApp.server.shutdown();
        displayMessage("Server is down");
        runButton.setDisable(false);
        shutdownButton.setDisable(true);
        //     }
        // }));
    }

    public void handleShowHistoryButton() {
        sceneDispatcher.openHistoryWindow();
    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
    }

    public void updateRoomList(ArrayList<String> rooms) { ///TODO fix this shit
        Platform.runLater(() -> {
            roomChoice.getItems().clear();
            for (String room : rooms) {
                roomChoice.getItems().add(room);
            }
            roomChoice.setValue("MainRoom");
        });
    }




}
