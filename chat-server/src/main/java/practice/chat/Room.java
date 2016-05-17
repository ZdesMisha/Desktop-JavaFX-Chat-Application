package practice.chat;


import practice.chat.protocol.shared.message.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;

    private Queue<Message> recentHistory = new LinkedList<>();
    private ArrayList<Client> users = new ArrayList<>();


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
        saveMessageInHistory(message);
    }

    public synchronized boolean isEmptyRoom() {
        return users.isEmpty();
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }

    public synchronized void addUserToMainRoom(Socket socket) {
        Client client = new Client(socket, this);
        client.onInit(() -> sendRecentHistory(client));
        client.start();
        users.add(client);
        client.setRoom(chat.rooms.get("MainRoom"));
    }

    public synchronized void addUser(Client client) {
        users.add(client);
        client.setRoom(this);
        broadcastRoomMessage(new TextMessage(client.getLogin() + " has joined the chat"));
        broadcastRoomMessage(new RoomList(chat.prepareRoomList()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
        client.sendMessage(new RoomRequest(this.getName())); //TODO may there is a better way to inform client about his current room?
    }

    public synchronized void removeUser(Client client) {
        users.remove(client);
        broadcastRoomMessage(new Logout(client.getLogin()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }

    public synchronized ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (Client user : users) {
            userList.add(user.getLogin());
        }
        return userList;
    }

    private synchronized void saveMessageInHistory(Message message) {
        if (recentHistory.size() == 10) {
            recentHistory.poll();
        }
        recentHistory.offer(message);
    }

    private synchronized void sendRecentHistory(Client client){
       for(Message message:recentHistory){
           client.sendMessage(message);
       }
    }

    public void stop() { //TODO impementation
    }

    public String getName() {
        return name;
    }

    public Chat getChat() {
        return chat;
    }

}
