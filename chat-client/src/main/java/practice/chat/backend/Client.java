package practice.chat.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.controller.ChatController;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.messages.TextMessage;
import practice.chat.protocol.shared.messages.request.Login;
import practice.chat.protocol.shared.messages.response.info.CurrentRoom;
import practice.chat.protocol.shared.messages.response.info.RoomList;
import practice.chat.protocol.shared.messages.response.info.UserList;
import practice.chat.protocol.shared.messages.response.info.ViolatedLoginUniqueConstraint;
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private final String login;
    private final String ip;
    private final int port;
    private final ChatController chatController;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private volatile boolean connected;

    public Client(String login, String ip, int port, ChatController chatController) {
        this.login = login;
        this.ip = ip;
        this.port = port;
        this.chatController = chatController;
    }

    public void close() {
        synchronized (this) {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(socket);
        }

        connected = false;
    }

    public void establishConnection() throws Exception {

        InetAddress address = InetAddress.getByName(ip);

        socket = new Socket(address, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());

        connected = true;
    }

    public void sendMessage(Message message) {
        if (!connected) {
            throw new IllegalStateException("Connection not established");
        }
        try {
            synchronized (this) {
                output.writeObject(message);
            }
        } catch (IOException ex) {
            LOG.error("Failure during sending message {}", message, ex);
        }
    }

    public void run() {
        if (!connected) {
            throw new IllegalStateException("Connection not established");
        }
        String disconnectReason = "Disconnected";
        try {
            sendMessage(new Login(login));
            while (connected) {
                ProcessResult processResult = processMessage((Message) input.readObject());
                if (processResult.disconnectRequested) {
                    connected = false;
                    disconnectReason = processResult.disconnectReason;
                }
            }
        } catch (Exception ex) {
            LOG.error("Connection failure occurred during message listening", ex);
        } finally {
            chatController.onDisconnect(disconnectReason);
            close();
        }
    }

    private ProcessResult processMessage(Message message) {

        if (message instanceof ViolatedLoginUniqueConstraint) {

            return new ProcessResult(true,
                    "Disconnect\n" + ((ViolatedLoginUniqueConstraint) message).getLogin() + " login is already in use");

        } else if (message instanceof UserList) {

            chatController.updateUserList(((UserList) message).getUserList());

        } else if (message instanceof RoomList) {

            chatController.updateRoomList(((RoomList) message).getRoomList());

        } else if (message instanceof CurrentRoom) {

            chatController.updateRoom(((CurrentRoom) message).getRoomName());

        } else if (message instanceof TextMessage) {

            chatController.displayMessage((TextMessage) message);

        }
        return new ProcessResult();
    }

    private class ProcessResult {

        private boolean disconnectRequested;
        private String disconnectReason;

        ProcessResult() {
        }

        ProcessResult(boolean disconnectRequested, String disconnectReason) {
            this.disconnectRequested = disconnectRequested;
            this.disconnectReason = disconnectReason;
        }
    }
}
