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
    private int port;
    private Socket socket;
    //private String room;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ChatController chatController;


    public Client(String login, String ip, int port, ChatController chatController) {
        this.login = login;
        this.ip = ip;
        this.port = port;
        this.chatController = chatController;
    }

    public void close() { //TODO send logout message
        try {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void establishConnection() {
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
            socket = new Socket(address, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            System.out.println("Can not establish connection to backend");
            ex.printStackTrace();
        } finally {
            //Platform.runLater(() -> chatController.handleBrokenConnection());
        }
    }

    public void sendMessage(Message message) throws IOException {
        output.writeObject(message);
    }


    public void run() {
        Message message;
        try {
            sendMessage(new Login(login));
            while (true) {
                message = (Message) input.readObject();
                System.out.println(message);
                processMessage(message);
            }
        } catch (Exception ex) {
            System.out.println("Server listener failure!"); //TODO logger
            ex.printStackTrace();
        } finally {
            Platform.runLater(() -> chatController.handleBrokenConnection());
            close();
        }
    }

    private void processMessage(Message message) {

        if (message instanceof UserList) {

            chatController.updateUserList(((UserList) message).getUserList());

        } else if (message instanceof RoomList) {

            chatController.updateRoomList(((RoomList) message).getRoomList());

        } else if (message instanceof CurrentRoom) {

            //room = ((RoomRequest) message).getRoomName();
            String room = ((CurrentRoom) message).getRoomName(); //TODO if String room really unnecessary?
            chatController.updateRoomNameLabel(room);

        } else {

            chatController.displayMessage((TextMessage) message);

        }
    }
}
