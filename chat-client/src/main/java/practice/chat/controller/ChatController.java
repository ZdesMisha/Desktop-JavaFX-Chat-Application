package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import practice.chat.main.SceneDispatcher;
import practice.chat.protocol.shared.message.MessageImpl;
import practice.chat.main.MainApp;

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

    public  ChoiceBox<String> roomChoice;

    private SceneDispatcher sceneDispatcher;

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    public void handleCreateRoomButton(){
        try {
            MainApp.client.createNewRoom();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void handleChangeRoomButton(){
        try {
            MainApp.client.changeRoom(roomChoice.getValue());
        }catch(IOException ex){
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

    public void displayMessage(MessageImpl userMessage) {
        chatDisplay.appendText(userMessage.getMessage() + "\n");
    }

    public void handleBrokenConnection() {
        sceneDispatcher.switchToLogin();
    }

    public void updateUserList(ArrayList<String> users) {
        userList.clear();
        for (String user : users) {
            userList.appendText(user + "\n");
        }
    }

    public void updateRoomList(ArrayList<String> rooms){
        roomChoice.getItems().removeAll();
        for (String room : rooms){
            roomChoice.getItems().add(room);
        }
    }

}
