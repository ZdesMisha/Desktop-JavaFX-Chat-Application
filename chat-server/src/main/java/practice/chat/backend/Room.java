package practice.chat.backend;


import practice.chat.history.HistoryWriter;
import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.TextMessage;
import practice.chat.protocol.shared.message.response.info.CurrentRoom;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.info.UserList;
import practice.chat.protocol.shared.message.response.text.NewUser;
import practice.chat.protocol.shared.message.response.text.UserLeft;

import java.util.*;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    public static final int FILE_SIZE = 100;
    public static final int HISTORY_SIZE = 10;
    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;
    private HistoryWriter historyManager = HistoryWriter.getInstance();

    private final List<Client> users = new ArrayList<>();
    private final List<Message> history = new ArrayList<>();
    private final Queue<Message> recentMessages = new LinkedList<>();


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

    public void broadcastMessage(Message message) {
        synchronized (users) {
            for (Client client : users) {
                client.sendMessage(message);
            }
        }
    }

    public boolean isEmpty() {
        synchronized (users) {
            return users.isEmpty();
        }
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }


    public void addUser(Client client) {
        sendRecentMessages(client);
        client.sendMessage(new CurrentRoom(this.getName()));
        client.sendMessage(new RoomList(chat.prepareRoomList()));
        synchronized (users) {
            users.add(client);
        }
        client.setRoom(this);
        NewUser loginMessage = new NewUser(client.getLogin(), new Date());
        saveMessageInQueue(loginMessage);
        broadcastMessage(loginMessage);
        broadcastMessage(new UserList(prepareUserList()));
    }


    public void removeUser(Client client) {
        synchronized (users) {
            users.remove(client);
        }
        UserLeft logoutMessage = new UserLeft(client.getLogin(), new Date());
        saveMessageInQueue(logoutMessage);
        broadcastMessage(logoutMessage);
        broadcastMessage(new UserList(prepareUserList()));
    }

    private ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        synchronized (users) {
            for (Client user : users) {
                userList.add(user.getLogin());
            }
        }
        return userList;
    }

    public synchronized void saveMessageInQueue(TextMessage message) {
        recentMessages.offer(message);
        if (recentMessages.size() >= HISTORY_SIZE) {
            history.add(recentMessages.poll());
        }
        if (history.size() >= FILE_SIZE) {
            saveHistory();
        }
    }

    public synchronized void saveHistory() {
        history.addAll(recentMessages);
        historyManager.writeHistory(history, this.getName());
        history.clear();
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
        saveHistory();
        synchronized (users) {
            users.forEach(Client::closeConnection);
        }
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
