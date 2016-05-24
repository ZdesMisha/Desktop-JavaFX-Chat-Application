package practice.chat.backend;

import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.request.Login;
import practice.chat.protocol.shared.message.response.info.CurrentRoom;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.info.UserList;
import practice.chat.protocol.shared.message.response.info.ViolatedLoginUniqueConstraint;
import practice.chat.utils.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread {

    private String login;
    private String ip;
    private short port;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ChatController chatController;


    public Client(String login, String ip, short port, ChatController chatController) {
        this.login = login;
        this.ip = ip;
        this.port = port;
        this.chatController = chatController;
    }

    public void close() { //TODO logger
        IOUtils.closeQuietly(output);
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(socket);
    }

    public void establishConnection() throws Exception {
        InetAddress address;
        address = InetAddress.getByName(ip);
        socket = new Socket(address, port);
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            ex.printStackTrace(); //TODO logger
        }
    }

    public void run() {
        Message message;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            sendMessage(new Login(login));

            while (true) {
                message = (Message) input.readObject();
                processMessage(message);
            }
        } catch (Exception ex) { //TODO logger
            ex.printStackTrace();
        } finally {
            Platform.runLater(() -> chatController.onDisconnect());
            close(); //TODO how to avoid it?
        }
    }
    private void processMessage(Message message) {

        if (message instanceof ViolatedLoginUniqueConstraint) {

            close(); //TODO notify client about error message

        } else if (message instanceof UserList) {

            Platform.runLater(() ->chatController.updateUserList(((UserList) message).getUserList()));

        } else if (message instanceof RoomList) {

            Platform.runLater(() ->chatController.updateRoomList(((RoomList) message).getRoomList()));

        } else if (message instanceof CurrentRoom) {

            String room = ((CurrentRoom) message).getRoomName();
            Platform.runLater(() ->chatController.updateRoom(room));

        } else if (message instanceof TextMessage) {

            chatController.displayMessage((TextMessage) message);

        }
    }
}
