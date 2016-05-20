package practice.chat.backend;


import practice.chat.history.HistoryManager;
import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.info.OnlineUserList;
import practice.chat.protocol.shared.message.info.RoomList;
import practice.chat.protocol.shared.message.info.RoomRequest;
import practice.chat.protocol.shared.message.common.Login;
import practice.chat.protocol.shared.message.common.Logout;
import practice.chat.protocol.shared.message.common.TextMessage;

import java.util.*;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;
    private HistoryManager historyManager = HistoryManager.getInstance();

    private final ArrayList<Message> recentHistory = new ArrayList<>();
    private final Queue<Message> recentMessages = new LinkedList<>();
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


    public void addUser(Client client) {
        sendRecentMessages(client);
        client.sendMessage(new RoomRequest(this.getName()));
        client.sendMessage(new RoomList(chat.prepareRoomList()));
        synchronized (users) {
            users.add(client);
        }
        client.setRoom(this);
        TextMessage loginMessage = new Login(client.getLogin());
        loginMessage.setDate(new Date());
        saveMessageInQueue(loginMessage);
        broadcastRoomMessage(loginMessage);
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }


    public void removeUser(Client client) {
        synchronized (users) {
            users.remove(client);
        }
        TextMessage logoutMessage = new Logout(client.getLogin());
        logoutMessage.setDate(new Date());
        saveMessageInQueue(logoutMessage);
        broadcastRoomMessage(logoutMessage);
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }

    private synchronized ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (Client user : users) {
            userList.add(user.getLogin());
        }
        return userList;
    }

    public void saveMessageInQueue(Message message) {
        synchronized (recentMessages) {
            recentMessages.offer(message);
            if (recentMessages.size() >= 10) {
                recentHistory.add(recentMessages.poll());
            } else {
                recentHistory.add(recentMessages.peek());
            }
            if (recentHistory.size() >= 20) {
                saveHistory(recentHistory);
            }
        }
    }

    private void saveHistory(ArrayList<Message> history) {
        historyManager.writeHistoryToFile(recentHistory, this.getName());
        recentHistory.clear();
    }

    private void sendRecentMessages(Client client) {
        synchronized (recentMessages) {
            recentMessages.forEach(client::sendMessage);
        }
    }

    public String getName() {
        return name;
    }

    public void closeClientConnections() {
        users.forEach(Client::closeConnection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return name != null ? name.equals(room.name) : room.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
