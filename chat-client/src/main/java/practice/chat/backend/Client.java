package practice.chat.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Platform;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.messages.TextMessage;
import practice.chat.protocol.shared.messages.request.Login;
import practice.chat.protocol.shared.messages.response.info.CurrentRoom;
import practice.chat.protocol.shared.messages.response.info.RoomList;
import practice.chat.protocol.shared.messages.response.info.UserList;
import practice.chat.protocol.shared.messages.response.info.ViolatedLoginUniqueConstraint;
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
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);


    public Client(String login, String ip, short port, ChatController chatController) {
        this.login = login;
        this.ip = ip;
        this.port = port;
        this.chatController = chatController;
    }

    public void close() {
        IOUtils.closeQuietly(output);
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(socket);
    }

    public void establishConnection() throws Exception {
        InetAddress address;
        address = InetAddress.getByName(ip);
        socket = new Socket(address, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            LOG.error("Failure during sending message " + message);
            LOG.error("Error stack:\n" + ex);
        }
    }

    public void run() {
        Message message;
        try {
            sendMessage(new Login(login));
            while (true) {
                message = (Message) input.readObject();
                processMessage(message);
            }
        } catch (Exception ex) {
            LOG.error("Connection failure occurred during message listening");
            LOG.error("Error stack:\n" + ex);
        } finally {
            chatController.onDisconnect();
            close(); //TODO how to avoid it?
        }
    }

    private void processMessage(Message message) {

        if (message instanceof ViolatedLoginUniqueConstraint) {

            close(); //TODO notify client about error message


        } else if (message instanceof UserList) {

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
