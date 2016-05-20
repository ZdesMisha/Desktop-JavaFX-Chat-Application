package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import practice.chat.history.HistoryManager;
import practice.chat.main.SceneDispatcher;
import practice.chat.main.ServerApp;
import practice.chat.backend.Server;

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
        displayMessage("Starting backend...");
        ServerApp.server = new Server(sceneDispatcher.getServerController());
        ServerApp.server.start();
        updateRoomList(historyManager.getHistoryFileNames());
        runButton.setDisable(true);
        shutdownButton.setDisable(false);

    }

    public void handleUpdateHistoryListButton() {
        updateRoomList(historyManager.getHistoryFileNames());
    }

    public void handleShutdownButton() {
        // Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { //TODO implement it somewhere...
        //    public void run() {
        displayMessage("Shutdown backend...");
        ServerApp.server.shutdown();
        displayMessage("Server is down");
        runButton.setDisable(false);
        shutdownButton.setDisable(true);
        //     }
        // }));
    }

    public void handleShowHistoryButton() {
        if (roomChoice.getValue()!=null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You have to chose room!");
            alert.show();
        }
        sceneDispatcher.openHistoryWindow();
        //sceneDispatcher.setRoomName(roomChoice.getValue());

    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
    }

    private void updateRoomList(ArrayList<String> rooms) { ///TODO fix this shit
        Platform.runLater(() -> {
            roomChoice.getItems().clear();
            for (String room : rooms) {
                roomChoice.getItems().add(room);
            }
            roomChoice.setValue("MainRoom");
        });
    }


}
