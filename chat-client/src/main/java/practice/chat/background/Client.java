package practice.chat.background;

import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.ChangeRoom;
import practice.chat.protocol.shared.message.CreateNewRoom;
import practice.chat.protocol.shared.message.Login;
import practice.chat.protocol.shared.message.MessageImplementation;
import practice.chat.protocol.shared.message.OnlineUserList;
import practice.chat.protocol.shared.message.RoomList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client {

    private ServerConnection serverConnection;
    private String login;
    private String room;
    private ChatController chatController;


    public Client(String login, ChatController chatController) {
        this.login = login;
        this.chatController = chatController;
    }

    public void start() throws Exception {
        serverConnection = new ServerConnection();
        serverConnection.establishConnection();
        serverConnection.start();
    }

    public void close() {
        try {
            serverConnection.output.close();
            serverConnection.input.close();
            serverConnection.socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage(Message message) { //TODO implement universal message sender
    }

    public void sendTextMessage(String message) throws IOException {
        serverConnection.output.writeObject(new TextMessage(login, message));
    }

    public void sendLoginMessage() throws IOException {
        serverConnection.output.writeObject(new Login(login));
    }

    public void sendCreateRoomMessage() throws IOException {
        serverConnection.output.writeObject(new CreateNewRoom());
    }

    public void sendChangeRoomMessage(String room) throws IOException {
        serverConnection.output.writeObject(new ChangeRoom(login, room));
    }

    private class ServerConnection extends Thread {

        private static final String IP = "127.0.0.1";
        private static final int PORT = 1234;

        private Socket socket;
        private InetAddress address;
        private Message message;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public void establishConnection() throws Exception {
            address = InetAddress.getByName(IP);
            socket = new Socket(address, PORT);
        }

        public void run() {

            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                sendLoginMessage();
                while (true) {
                    message = (Message) input.readObject();
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

        public void processMessage(Message message) {
            if (message instanceof OnlineUserList) {

                chatController.updateUserList(((OnlineUserList) message).getUserList());

            } else if (message instanceof RoomList) {

                chatController.updateRoomList(((RoomList) message).getRoomList());

            } else {

                chatController.displayMessage((MessageImplementation) message);

            }
        }
    }


}
