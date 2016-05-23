package practice.chat.backend;

import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.request.ChangeRoom;
import practice.chat.protocol.shared.message.request.CreateRoom;
import practice.chat.protocol.shared.message.request.Login;
import practice.chat.protocol.shared.message.request.Logout;
import practice.chat.protocol.shared.message.response.info.ViolatedLoginUniqueConstraint;
import practice.chat.utils.ResourceCloser;

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

    public Client(Socket socket) {
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
        } catch (Exception ex) { //TODO logger
            ex.printStackTrace();
        } finally {
            chat.removeUserFromChat(this);
            closeConnection();//TODO logger
        }
    }

    private void processMessage(Message message) {

        if (message instanceof ChangeRoom) {

            String room = ((ChangeRoom) message).getRoom();
            chat.changeRoom(this, chat.rooms.get(room));

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

    public void HowToNameThisMethod(){
        sendMessage(new ViolatedLoginUniqueConstraint(login));
    }

    public void closeConnection() { //TODO logger
        ResourceCloser.closeQuietly(output);
        ResourceCloser.closeQuietly(input);
        ResourceCloser.closeQuietly(socket);
    }

    public void sendMessage(Message message) {
        try {
                output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace(); //TODO logger
        }
    }

    public String getLogin() {
        return login;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


}
