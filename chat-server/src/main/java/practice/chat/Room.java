package practice.chat;


import practice.chat.protocol.shared.message.*;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean mainRoomFlag;
    private static int roomCounter = 1;
    private String name;

    ArrayList<Client> users = new ArrayList<>();

    public Room(Chat chat) {
        this.name = "Room " + roomCounter++;
        mainRoomFlag = false;
        this.chat = chat;
    }

    public Room(String name, Chat chat) {
        this.name = name;
        roomCounter++;
        mainRoomFlag = true;
        this.chat = chat;
    }

    @Override
    public String toString() {
        return name + " with users: " + users;
    }

    public void broadcastMessage(Message message) {
        for (Client client : users) {
            client.sendMessage(message);
        }
    }

    public boolean isEmptyRoom(Room room) {
        return users.isEmpty();
    }

    public boolean isMainRoom() {
        return mainRoomFlag;
    }

    public void addUserToMainRoom(Socket socket) {
        Client client = new Client(socket, this);
        users.add(client);
        client.start();
        client.setRoom(chat.rooms.get("MainRoom"));
    }

    public void switchUserToRoom(Room room) {

    }

    public void addUser(Client client) {
        users.add(client);
    }

    public void removeUser(Client client) {
        users.remove(client);
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
    public Room getRoomByName(String name){
        return chat.rooms.get(name);
    }

}
