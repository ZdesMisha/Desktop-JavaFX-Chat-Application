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
import practice.chat.protocol.shared.messages.TextMessage;
import practice.chat.protocol.shared.messages.common.UserTextMessage;
import practice.chat.protocol.shared.messages.request.ChangeRoom;
import practice.chat.protocol.shared.messages.request.CreateRoom;
import practice.chat.protocol.shared.messages.response.text.NewUser;
import practice.chat.protocol.shared.messages.response.text.UserLeft;

import java.util.List;

/**
 * Created by misha on 06.05.16.
 */
public class ChatController {

    private static final int MAX_MESSAGE_LENGTH = 300;

    private static final TextFormatter<String> textLengthFormatter = new TextFormatter<>(c -> c
            .getControlNewText().length() > MAX_MESSAGE_LENGTH ? null : c);

    @FXML
    private TextFlow chatDisplay;
    @FXML
    private TextField textField;
    @FXML
    private TextArea userList;
    @FXML
    private Label userAmount;
    @FXML
    private ScrollPane inputTextScroll;
    @FXML
    private Label room;
    @FXML
    private Label login;
    @FXML
    private ChoiceBox<String> roomChoice;

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
        applicationDispatcher.getClient().sendMessage(new CreateRoom());

    }

    public void handleChangeRoomButton() {
        String currentRoom = room.getText();
        String roomToJoin = roomChoice.getValue();
        if (currentRoom.equals(roomToJoin)) {
            return;
        }
        userList.clear();
        chatDisplay.getChildren().clear();
        applicationDispatcher.getClient().sendMessage(new ChangeRoom(roomToJoin));

    }

    public void handleLogoutButton() {
        applicationDispatcher.getClient().close();
    }

    public void handleSendButton() {
        String message = textField.getText();
        if (isValidMessage(message)) {
            applicationDispatcher.getClient().sendMessage(new UserTextMessage(login.getText(), message));
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

    public void onDisconnect(String error) {
        applicationDispatcher.switchToLogin();
        Platform.runLater(() -> {
            userList.clear();
            chatDisplay.getChildren().clear();
            roomChoice.getItems().clear();
            showAlertBox(error);
        });
    }

    public void updateUserList(List<String> users) {
        Platform.runLater(() -> {
            userList.clear();
            for (String user : users) {
                userList.appendText(user + "\n");
            }
            userAmount.setText("" + users.size());
        });
    }

    public void updateRoomList(List<String> rooms) {
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

    public void setLogin(String login) {
        this.login.setText(login);
    }
}
