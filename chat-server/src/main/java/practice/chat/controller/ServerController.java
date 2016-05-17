package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import practice.chat.main.ServerApp;
import practice.chat.server.Server;

import java.util.ArrayList;

/**
 * Created by misha on 17.05.16.
 */
public class ServerController {

    @FXML
    private TextArea serverLog;

    public ChoiceBox<String> roomChoice;


    public void handleRunButton() {
        displayMessage("Starting server...");
        ServerApp.server = new Server(ServerApp.serverController);
        ServerApp.server.start();
    }

    public void handleShutdownButton() {
        // Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { //TODO implement it somethere...
        //    public void run() {
        displayMessage("Shutdown server...");
        ServerApp.server.shutdown();
        displayMessage("Server is down");
        //     }
        // }));
    }

    public void handleShowHistoryButton() {
        System.out.println("HISTORY!");
    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
    }

    public void updateRoomList(ArrayList<String> rooms) {
        Platform.runLater(() -> {
            roomChoice.getItems().clear();
            for (String room : rooms) {
                roomChoice.getItems().add(room);
            }
            roomChoice.setValue("MainRoom");
        });
    }


}
