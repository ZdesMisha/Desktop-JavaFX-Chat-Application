package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import practice.chat.dispatcher.ApplicationDispatcher;
import practice.chat.protocol.shared.message.TextMessage;
import practice.chat.protocol.shared.message.common.SimpleTextMessage;
import practice.chat.protocol.shared.message.request.ChangeRoom;
import practice.chat.protocol.shared.message.request.CreateRoom;
import practice.chat.protocol.shared.message.response.text.NewUser;
import practice.chat.protocol.shared.message.response.text.UserLeft;

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

    private ApplicationDispatcher applicationDispatcher;

    @FXML
    public void initialize() {
        textField.setTextFormatter(textLengthFormatter);
        textField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                handleSendButton();
            }
        });
    }

    public void setApplicationDispatcher(ApplicationDispatcher applicationDispatcher) {
        this.applicationDispatcher = applicationDispatcher;
    }

    public void handleCreateRoomButton() {
        userList.clear();
        chatDisplay.getChildren().clear();
        applicationDispatcher.client.sendMessage(new CreateRoom(login.getText()));

    }

    public void handleChangeRoomButton() {
        userList.clear();
        chatDisplay.getChildren().clear();
        applicationDispatcher.client.sendMessage(new ChangeRoom(roomChoice.getValue()));

    }

    public void handleLogoutButton() {
        applicationDispatcher.client.close();

    }

    public void handleSendButton() {
        String message = textField.getText();
        if (isValidMessage(message)) {
            applicationDispatcher.client.sendMessage(new SimpleTextMessage(login.getText(), message));
            textField.clear();
        }
    }

    public void displayMessage(TextMessage message) {
        Text text = new Text(message.toString() + "\n");
        text.setFont(Font.font(11));
        if (message instanceof NewUser) {

            text.setFill(Color.GREEN);

        } else if (message instanceof UserLeft) {

            text.setFill(Color.DARKRED);

        } else if (message.getLogin().equals(login.getText())) {

            text.setFill(Color.DEEPSKYBLUE);
        }
        Platform.runLater(() -> {
            inputTextScroll.setVvalue(1.0);
            chatDisplay.getChildren().add(text);
        });
    }

    public void onDisconnect() { //TODO add notification about connection failure;
        applicationDispatcher.switchToLogin();
        showAlertBox("Disconnected");

    }

    public void updateUserList(ArrayList<String> users) {
        userList.clear();
        for (String user : users) {
            userList.appendText(user + "\n");
        }
        userAmount.setText(users.size() + "");
    }

    public void updateRoomList(ArrayList<String> rooms) {
        roomChoice.getItems().clear();
        for (String room : rooms) {
            roomChoice.getItems().add(room);
        }
        roomChoice.setValue(room.getText());
    }

    public void updateRoom(String roomName) {
        room.setText(roomName);
        roomChoice.setValue(roomName);
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
