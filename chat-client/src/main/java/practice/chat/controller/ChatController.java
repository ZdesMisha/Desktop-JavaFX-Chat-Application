package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import practice.chat.main.MainApp;
import practice.chat.protocol.shared.message.TextMessage;
import practice.chat.protocol.shared.message.common.SimpleTextMessage;
import practice.chat.protocol.shared.message.request.ChangeRoom;
import practice.chat.protocol.shared.message.request.CreateRoom;

import java.util.ArrayList;

/**
 * Created by misha on 06.05.16.
 */
public class ChatController {

    @FXML
    private TextFlow chatDisplay;
    @FXML
    private TextField textField;
    @FXML
    private TextArea userList;
    @FXML
    private Label userAmount;

    @FXML
    ScrollPane inputTextScroll;

    public Label room;

    public Label login;

    public ChoiceBox<String> roomChoice;


    private TextFormatter<String> textLengthFormatter = new TextFormatter<>(c -> c
            .getControlNewText().length() > 300 ? null : c);

    private SceneDispatcher sceneDispatcher;

    @FXML
    public void initialize() {
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
//        userList.clear(); //TODO????
//        chatDisplay.getChildren().clear();
        MainApp.client.sendMessage(new CreateRoom(login.getText()));

    }

    public void handleChangeRoomButton() {
//        userList.clear();//TODO????
//        chatDisplay.getChildren().clear();
        MainApp.client.sendMessage(new ChangeRoom(roomChoice.getValue()));

    }

    public void handleLogoutButton() {
        MainApp.client.shutdown();

    }

    public void handleSendButton() {
        String message = textField.getText();
        if (isValidMessage(message)) {
            MainApp.client.sendMessage(new SimpleTextMessage(login.getText(), message));
            textField.clear();
        }
    }

    public void displayMessage(TextMessage userMessage) {
        Text text = new Text(userMessage.toString() + "\n");
        text.setFont(Font.font(11));
        if (userMessage.getLogin().equals(login.getText())) {
            text.setFill(Color.DEEPSKYBLUE);
        }
        Platform.runLater(() -> {
            inputTextScroll.setVvalue(1.0);
            chatDisplay.getChildren().add(text);
        });
    }

    public void onDisconnect() { //TODO add notification about connection failure;
        sceneDispatcher.switchToLogin();
        showAlertBox("Connection closed");

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

    public void updateRoom(String roomName) {
        Platform.runLater(() -> {
            userList.clear(); //TODO????
            chatDisplay.getChildren().clear();
            room.setText(roomName);
            roomChoice.setValue(roomName);
        });
    }

    private boolean isValidMessage(String message) {
        return message.matches(".+");
    }

    private void showAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }


}
