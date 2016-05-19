package practice.chat.background;

import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.common.CreateNewRoom;
import practice.chat.protocol.shared.message.info.RoomRequest;
import practice.chat.protocol.shared.message.common.Login;
import practice.chat.protocol.shared.message.common.TextMessage;
import practice.chat.protocol.shared.message.info.OnlineUserList;
import practice.chat.protocol.shared.message.info.RoomList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client { //TODO consider if this class is really matters

    private ServerConnection serverConnection;
    private String login;
    private String ip;
    private int port;
    private String room = "MainRoom";
    private ChatController chatController;


    public Client(String login,String ip,int port, ChatController chatController) {
        this.login = login;
        this.ip=ip;
        this.port=port;
        this.chatController = chatController;
    }

    public void start() throws Exception {
        serverConnection = new ServerConnection(ip,port);
        serverConnection.establishConnection();
        serverConnection.start();
    }

    public void close() {
        try {
            if (serverConnection.output != null) {
                serverConnection.output.close();
            }
            if (serverConnection.input != null) {
                serverConnection.input.close();
            }
            if (serverConnection.socket != null) {
                serverConnection.socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        serverConnection.output.writeObject(message);
    }

    private class ServerConnection extends Thread {

        private String ip;
        private int port;
        private Socket socket;
        private InetAddress address;
        private Message message;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ServerConnection(String ip,int port){
            this.ip=ip;
            this.port=port;
        }

        public void establishConnection() throws Exception {
            address = InetAddress.getByName(ip);
            socket = new Socket(address, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        }

        public void run() {

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

        public void processMessage(Message message) {

            if (message instanceof OnlineUserList) {

                chatController.updateUserList(((OnlineUserList) message).getUserList());

            } else if (message instanceof RoomList) {

                chatController.updateRoomList(((RoomList) message).getRoomList());

            } else if (message instanceof RoomRequest) {

                room = ((RoomRequest) message).getRoomName();
                chatController.updateRoomNameLabel(room);

            } else {

                chatController.displayMessage((TextMessage) message);

            }
        }
    }


}
