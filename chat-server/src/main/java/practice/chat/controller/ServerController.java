package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import practice.chat.history.HistoryReader;
import practice.chat.history.HistoryWriter;
import practice.chat.main.ServerApp;
import practice.chat.backend.Server;

import java.util.List;

/**
 * Created by misha on 17.05.16.
 */
public class ServerController {

    private HistoryReader historyReader = HistoryReader.getInstance();

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

    @FXML
    public void initialize(){
        updateRoomList(historyReader.getHistoryFileNames());
    }


    public void handleRunButton() {
        displayMessage("Starting server...");
        ServerApp.server = new Server(sceneDispatcher.getServerController());
        ServerApp.server.start();
        runButton.setDisable(true);
        shutdownButton.setDisable(false);

    }

    public void handleUpdateHistoryListButton() {
        updateRoomList(historyReader.getHistoryFileNames());
    }

    public void handleShutdownButton() {
        displayMessage("Shutdown server...");
        ServerApp.server.shutdown();
        displayMessage("Server is down");
        runButton.setDisable(false);
        shutdownButton.setDisable(true);
    }

    public void handleShowHistoryButton() {
        if (roomChoice.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You have to chose room!");
            alert.show();
        }
        sceneDispatcher.openHistoryWindow(roomChoice.getValue());

    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
    }

    private void updateRoomList(List<String> rooms) {
        Platform.runLater(() -> {
            roomChoice.getItems().clear();
            for (String room : rooms) {
                roomChoice.getItems().add(room);
            }
            roomChoice.setValue("MainRoom");///TODO fix this shit
        });
    }


}
