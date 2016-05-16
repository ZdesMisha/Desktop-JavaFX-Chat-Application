package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import practice.chat.main.SceneDispatcher;
import practice.chat.protocol.shared.message.MessageImplementation;
import practice.chat.main.MainApp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by misha on 06.05.16.
 */
public class ChatController {

    @FXML
    private TextArea chatDisplay;
    @FXML
    private TextField textField;
    @FXML
    private TextArea userList;
    @FXML
    private Label userAmount;

    public  Label room;

    public Label login;

    public ChoiceBox<String> roomChoice;

    private SceneDispatcher sceneDispatcher;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");



    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    public void handleCreateRoomButton() {
        userList.clear();
        chatDisplay.clear();
        try {
            MainApp.client.sendCreateRoomMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleChangeRoomButton() {
        userList.clear();
        chatDisplay.clear();
        try {
            MainApp.client.sendChangeRoomMessage(roomChoice.getValue());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleLogoutButton() {
        sceneDispatcher.switchToLogin();
        MainApp.client.close();
    }

    public void handleSendButton() {
        String message = textField.getText();
        try {
            MainApp.client.sendTextMessage(message);
            textField.clear();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void displayMessage(MessageImplementation userMessage) {
        String date = sdf.format(new Date());
        chatDisplay.appendText("[" + date + "]\n" + userMessage.getMessage() + "\n");
    }

    public void handleBrokenConnection() {
        sceneDispatcher.switchToLogin();
    }

    public void updateUserList(ArrayList<String> users) {
        Platform.runLater(() -> {
            userList.clear();
            for (String user : users) {
                userList.appendText(user + "\n");
            }
            userAmount.setText(users.size() + "");
        });
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

    public void updateRoomNameLable(String roomName){
        Platform.runLater(() -> {
          room.setText(roomName);
        });
    }

}
