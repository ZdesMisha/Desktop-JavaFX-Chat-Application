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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    public static final int FILE_SIZE = 100;
    public static final int HISTORY_SIZE = 10;
    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1; //TODO volatile?
    private String name;
    private HistoryWriter historyManager = HistoryWriter.getInstance();

    private final List<Client> users = new CopyOnWriteArrayList<>();
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
        for (Client client : users) {
            client.sendMessage(message);
        }
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }


    public void addUser(Client client) {
        sendRecentMessages(client);
        client.sendMessage(new CurrentRoom(this.getName()));
        client.sendMessage(new RoomList(chat.prepareRoomList()));
        users.add(client);
        client.setRoom(this);
        NewUser loginMessage = new NewUser(client.getLogin(), new Date());
        saveMessageInQueue(loginMessage);
        broadcastMessage(loginMessage);
        broadcastMessage(new UserList(prepareUserList()));
    }


    public void removeUser(Client client) {
        users.remove(client);
        UserLeft logoutMessage = new UserLeft(client.getLogin(), new Date());
        saveMessageInQueue(logoutMessage);
        broadcastMessage(logoutMessage);
        broadcastMessage(new UserList(prepareUserList()));
    }

    private List<String> prepareUserList() {
        return users.stream().map(Client::getLogin).collect(Collectors.toList());
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

    private synchronized void sendRecentMessages(Client client) {
        recentMessages.forEach(client::sendMessage);
    }

    public String getName() {
        return name;
    }

    public void closeClientConnections() {
        saveHistory();
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
