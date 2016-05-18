package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import practice.chat.history.HistoryManager;
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

    public ChoiceBox<String> roomChoice;


    public void handleRunButton() {
        displayMessage("Starting server...");
        ServerApp.server = new Server(ServerApp.serverController);
        ServerApp.server.start();
        updateRoomList(historyManager.getHistoryFileNames());
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
        //     }
        // }));
    }

    public void handleShowHistoryButton() {
        Stage window = new Stage();
        window.setTitle("Room history");
        window.setMinWidth(250);
        window.setMinHeight(500);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setMinHeight(490);
        textArea.setMinWidth(240);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(textArea);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        displayHistory(textArea);
        window.show();
    }

    public void displayHistory(TextArea textArea) {
        String room = roomChoice.getValue();
        for (String line : historyManager.getRoomHistory(room)){
            textArea.appendText(line+"\n");
        }
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
