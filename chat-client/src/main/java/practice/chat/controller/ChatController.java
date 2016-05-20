package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import practice.chat.main.SceneDispatcher;
import practice.chat.main.MainApp;
import practice.chat.protocol.shared.message.TextMessage;
import practice.chat.protocol.shared.message.common.SimpleTextMessage;
import practice.chat.protocol.shared.message.request.ChangeRoom;
import practice.chat.protocol.shared.message.request.CreateRoom;

import java.io.IOException;
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
        Platform.runLater(()->chatDisplay.getChildren().clear());
        try {
            MainApp.client.sendMessage(new CreateRoom(login.getText()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleChangeRoomButton() {
        userList.clear();
        Platform.runLater(()->chatDisplay.getChildren().clear());
        try {
            MainApp.client.sendMessage(new ChangeRoom(roomChoice.getValue()));
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
                MainApp.client.sendMessage(new SimpleTextMessage(login.getText(), message));
                textField.clear();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void displayMessage(TextMessage userMessage) {
        Text text = new Text(userMessage.toString() + "\n");
        if (userMessage.getLogin()!=null && userMessage.getLogin().equals(login.getText())) {
            text.setStyle("-fx-fill: BLUE;-fx-font-weight:normal;");
            //chatDisplay.appendText(t2 + "\n");
        }
        //chatDisplay.appendText(userMessage + "\n");
        Platform.runLater(() -> {
            inputTextScroll.setVvalue(1.0);
            chatDisplay.getChildren().add(text);

        });

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
