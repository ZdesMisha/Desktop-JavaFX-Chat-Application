package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import practice.chat.main.SceneDispatcher;
import practice.chat.protocol.shared.message.common.ChangeRoom;
import practice.chat.protocol.shared.message.common.CreateNewRoom;
import practice.chat.protocol.shared.message.common.TextMessage;
import practice.chat.main.MainApp;
import practice.chat.protocol.shared.message.common.Text;

import java.io.IOException;
import java.util.ArrayList;

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

    public Label room;

    public Label login;

    public ChoiceBox<String> roomChoice;

    private TextFormatter<String> textLengthFormatter = new TextFormatter<>(c -> c
            .getControlNewText().length() > 300 ? null : c);

    private SceneDispatcher sceneDispatcher;

    @FXML
    public void initialize() { ///TODO may be it would be better to move something else to initializer?
        textField.setTextFormatter(textLengthFormatter);
        textField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                handleSendButton();
            }
        });
    }

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    public void handleCreateRoomButton() {
        userList.clear();
        chatDisplay.clear();
        try {
            MainApp.client.sendMessage(new CreateNewRoom(login.getText()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleChangeRoomButton() {
        userList.clear();
        chatDisplay.clear();
        try {
            MainApp.client.sendMessage(new ChangeRoom(login.getText(), roomChoice.getValue()));
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
        if (isValidMessage(message)) {
            try {
                MainApp.client.sendMessage(new Text(login.getText(), message));
                textField.clear();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void displayMessage(TextMessage userMessage) {
        chatDisplay.appendText(userMessage + "\n");
    }

    public void handleBrokenConnection() { //TODO add notification about connection failure;
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
            roomChoice.setValue(room.getText());
        });
    }

    public void updateRoomNameLabel(String roomName) {
        Platform.runLater(() -> {
            room.setText(roomName);
            roomChoice.setValue(roomName); //TODO make sure it will always work correct
        });
    }

    private boolean isValidMessage(String message) {
        return message.matches(".+");
    }


}
