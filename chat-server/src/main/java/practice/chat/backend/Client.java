package practice.chat.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.messages.TextMessage;
import practice.chat.protocol.shared.messages.request.ChangeRoom;
import practice.chat.protocol.shared.messages.request.CreateRoom;
import practice.chat.protocol.shared.messages.request.Login;
import practice.chat.utils.IOUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Created by misha on 13.05.16.
 */
public class Client extends Thread {

    private Socket socket;
    private String login;
    private Room room;
    private Chat chat;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

     Client(Socket socket) {
        this.socket = socket;
        this.chat = Chat.getInstance();
    }

    @Override
    public void run() {
        Message message;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                message = (Message) input.readObject();
                processMessage(message);
            }
        } catch (Exception ex) {
            LOG.error("Connection failure with host: " + socket);
            LOG.error("Error stack:\n" + ex);
        } finally {
            chat.removeUserFromChat(this);
            closeConnection();
        }
    }

    private void processMessage(Message message) {

        if (message instanceof ChangeRoom) {

            String room = ((ChangeRoom) message).getRoom();
            chat.changeRoom(this, room);

        } else if (message instanceof Login) {

            login = ((Login) message).getLogin();
            chat.addToMainRoom(this);

        } else if (message instanceof CreateRoom) {

            chat.createNewRoom(this);

        } else if (message instanceof TextMessage) {

            ((TextMessage) message).setDate(new Date());
            room.broadcastMessage(message);
            room.saveMessageInQueue((TextMessage) message);

        }
    }

    void closeConnection() {
        synchronized (this) {
            IOUtils.closeQuietly(output);
        }
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(socket);
    }

    void sendMessage(Message message) {
        try {
            synchronized (this) {
                output.writeObject(message);
            }
        } catch (IOException ex) {
            LOG.error("Unable to send message " + message);
            LOG.error("Error stack:\n" + ex);
        }
    }

    String getLogin() {
        return login;
    }

    Room getRoom() {
        return room;
    }

    void setRoom(Room room) {
        this.room = room;
    }
     Socket getSocket() {
        return socket;
    }


}
