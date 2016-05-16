package practice.chat;


import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.Login;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;

    ArrayList<Client> users = new ArrayList<>();


    public Room(Chat chat) {
        name = "Room " + roomCounter++;
        this.chat = chat;
        isMainRoom = false;
    }

    public Room(String name, Chat chat) {
        this.name = name;
        this.chat = chat;
        isMainRoom = true;
    }

    @Override
    public String toString() {
        return name + " with users: " + users;
    }

    public void broadcastRoomMessage(Message message) {
        for (Client client : users) {
            client.sendMessage(message);
        }
    }

    public boolean isEmptyRoom() {
        return users.isEmpty();
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }

    public void addUserToMainRoom(Socket socket) {
        Client client = new Client(socket, this);
        users.add(client);
        client.start();
        client.setRoom(chat.rooms.get("MainRoom"));
    }

    public void addUser(Client client) {
        users.add(client);
        client.setRoom(this);
        broadcastRoomMessage(new TextMessage(client.getLogin() + " has joined the chat"));
        broadcastRoomMessage(new RoomList(chat.prepareRoomList()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
        client.sendMessage(new RoomRequest(this.getName())); //TODO may there is a better way to inform client about his current room?
    }

    public void removeUser(Client client) {
        users.remove(client);
        broadcastRoomMessage(new Logout(client.getLogin()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }

    public ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (Client user : users) {
            userList.add(user.getLogin());
        }
        return userList;
    }

    public void stop() { //TODO impementation
    }

    public String getName() {
        return name;
    }


    public Chat getChat() {
        return chat;
    }

    public ArrayList<Client> getUsers() {
        return users;
    }
}
