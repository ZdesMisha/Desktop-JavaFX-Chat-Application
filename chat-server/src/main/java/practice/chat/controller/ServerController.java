package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import practice.chat.main.ServerApp;
import practice.chat.server.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        updateRoomList(getRoomHistoryFiles());
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
        window.showAndWait();
    }

    public void displayHistory(TextArea textArea) { ///TODO fix this shit
        String fileName = roomChoice.getValue();
        File file = new File("/home/misha/ChatHistory/"+fileName);
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            String s;
            while ((s = input.readLine()) != null) {
                textArea.appendText(s+"\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
    }

    public void updateRoomList(ArrayList<String> rooms) { ///TODO fix this shit
        Platform.runLater(() -> {
            roomChoice.getItems().clear();
            for (String room : rooms) {
                roomChoice.getItems().add(room+".txt");
            }
            roomChoice.setValue("MainRoom.txt");
        });
    }

    public ArrayList<String> getRoomHistoryFiles() { ///TODO fix this shit
        File dir = new File("/home/misha/ChatHistory/");
        File[] files = dir.listFiles();
        ArrayList<String> fileList = new ArrayList<String>();
        for (File file : files) {
            if (file.getTotalSpace()<5000000){
                if (file.getName().matches("txt$")){
                    fileList.add(file.getName());
                }
            }
            fileList.add(file.getAbsolutePath());
        }
        return fileList;
    }



}
