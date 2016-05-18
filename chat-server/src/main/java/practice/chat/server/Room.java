package practice.chat.server;


import practice.chat.history.HistoryManager;
import practice.chat.protocol.shared.message.*;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;
    private HistoryManager historyManager = HistoryManager.getInstance();

    private final ArrayList<MessageImplementation> recentHistory = new ArrayList<>();
    private final ArrayList<Client> users = new ArrayList<>();


    public Room(Chat chat) {
        name = "Room_" + roomCounter++;
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
        synchronized (users) {
            for (Client client : users) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized boolean isEmptyRoom() {
        synchronized (users) {
            return users.isEmpty();
        }
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }

    public void addUserToMainRoom(Socket socket) {
        Client client = new Client(socket, this);
        client.onInit(() -> sendRecentMessages(client));
        client.start();
        synchronized (users) {
            users.add(client);
        }
        synchronized (chat.rooms) {
            client.setRoom(chat.rooms.get("MainRoom"));
        }
    }

    public synchronized void addUser(Client client) {
        users.add(client);
        client.setRoom(this);
        Message loginMessage = new TextMessage(client.getLogin() + " has joined the chat");
        broadcastRoomMessage(loginMessage);
        saveMessageInQueue(loginMessage);
        broadcastRoomMessage(new RoomList(chat.prepareRoomList()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
        client.sendMessage(new RoomRequest(this.getName())); //TODO may there is a better way to inform client about his current room?
    }

    public void removeUser(Client client) {
        synchronized (users) {
            users.remove(client);
        }
        Message logoutMessage = new Logout(client.getLogin());
        broadcastRoomMessage(logoutMessage);
        saveMessageInQueue(logoutMessage);
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }

    public synchronized ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (Client user : users) {
            userList.add(user.getLogin());
        }
        return userList;
    }

    public void saveMessageInQueue(Message message) {
        synchronized (recentHistory) {
            if (recentHistory.size() > 20) {
                historyManager.writeHistoryToFile(recentHistory, this.getName()); //TODO may here is a better solution
                ArrayList<MessageImplementation> temp = (ArrayList<MessageImplementation>)recentHistory.subList(recentHistory.size()-10,recentHistory.size()-1);
                recentHistory.clear();
                recentHistory.addAll(temp);
            }
            recentHistory.add((MessageImplementation) message);
        }
    }

    public synchronized void sendRecentMessages(Client client) {
        int size = recentHistory.size(); //TODO may here is a better solution
        if (size > 10) {
            for (int i = 1; i < 10; i++) {
                client.sendMessage(recentHistory.get(size - i));
            }
        } else {
            for (Message message : recentHistory) {
                client.sendMessage(message);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void closeClientConnections() {
        users.forEach(Client::closeConnection);
    }
}
