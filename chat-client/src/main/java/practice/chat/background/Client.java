package practice.chat.background;

import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.message.Login;
import practice.chat.protocol.shared.message.MessageTemplate;
import practice.chat.protocol.shared.message.TextMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client {

    private ServerConnection serverConnection;
    private String name;
    private ChatController chatController;


    public Client(String name, ChatController chatController) {
        this.name = name;
        this.chatController = chatController;
    }

    public void start() {
        serverConnection = new ServerConnection();
        serverConnection.establishConnection();
        serverConnection.start();
    }

    public void close() {
        try {
            serverConnection.socket.close();
        } catch (IOException ex) {
            System.out.println("Can't close socket");
            ex.printStackTrace();
        }
    }

    public void sendTextMessage(String message) throws IOException {
        serverConnection.output.writeObject(new TextMessage(name, message));
    }

    public void sendLoginMessage() throws IOException {
        serverConnection.output.writeObject(new Login(name));
    }

    private class ServerConnection extends Thread {

        private static final String IP = "127.0.0.1";
        private static final  int PORT = 1234;
        private Socket socket;
        private InetAddress address;
        private MessageTemplate messageTemplate;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public void establishConnection(){
            try {
                address = InetAddress.getByName(IP);
                socket = new Socket(address, PORT);
            }catch(Exception ex){
                System.out.println("Cant establish connection. Server is offline.");
                ex.printStackTrace();
            }
        }

        public void run() {

            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                sendLoginMessage();
                while (true) {
                    messageTemplate = (MessageTemplate) input.readObject();
                    chatController.displayMessage(messageTemplate);
                }
            } catch (Exception ex) {
                System.out.println("Server listener failure!");
                Platform.runLater(() -> chatController.handleBrokenConnection());
                ex.printStackTrace();
            } finally {
                close();
            }
        }
    }


}
