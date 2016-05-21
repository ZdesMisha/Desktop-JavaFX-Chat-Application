package practice.chat.backend;

import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.request.Login;
import practice.chat.protocol.shared.message.response.info.CurrentRoom;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.info.UserList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread {

    private String login;
    private String ip;
    private short port;
    private Socket socket;
    private boolean running;
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
        try {
            if (output != null) {
                output.close();
            } else if (input != null) {
                input.close();
            } else if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        running=true;
        Message message;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            sendMessage(new Login(login));

            while (running) {
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

    public void shutdown(){
        running=false;
        close();
    }

    private void processMessage(Message message) {

        if (message instanceof UserList) {

            chatController.updateUserList(((UserList) message).getUserList());

        } else if (message instanceof RoomList) {

            chatController.updateRoomList(((RoomList) message).getRoomList());

        } else if (message instanceof CurrentRoom) {

            String room = ((CurrentRoom) message).getRoomName();
            chatController.updateRoom(room);

        } else if (message instanceof TextMessage) {

            chatController.displayMessage((TextMessage) message);

        }
    }
}
