package practice.chat.backend;


import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.messages.TextMessage;
import practice.chat.protocol.shared.messages.response.info.CurrentRoom;
import practice.chat.protocol.shared.messages.response.info.RoomList;
import practice.chat.protocol.shared.messages.response.info.UserList;
import practice.chat.protocol.shared.messages.response.text.NewUser;
import practice.chat.protocol.shared.messages.response.text.UserLeft;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static practice.chat.history.HistoryWriter.writeHistory;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private static final int ENTIRE_HISTORY_SIZE = 100;
    private static final int RECENT_HISTORY_SIZE = 10;

    private Chat chat;
    private boolean isMainRoom;
    private String name;

    private final List<Client> users = new CopyOnWriteArrayList<>();
    private final AtomicReference<List<Message>> entireHistory = new AtomicReference<>(new CopyOnWriteArrayList<>());
    private final Queue<Message> recentHistory = Queues.synchronizedQueue(EvictingQueue.create(RECENT_HISTORY_SIZE));

    public Room(int roomNumber, Chat chat) {
        name = "Room_" + roomNumber;
        this.chat = chat;
        isMainRoom = false;
    }

    public Room(String name, Chat chat) {
        this.name = name;
        this.chat = chat;
        isMainRoom = true;
    }

    void broadcastMessage(Message message) {
        for (Client client : users) {
            client.sendMessage(message);
        }
    }

    boolean isEmpty() {
        return users.isEmpty();
    }

    boolean isMainRoom() {
        return isMainRoom;
    }

    void addUser(Client client) {
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

    void removeUser(Client client) {
        users.remove(client);
        UserLeft logoutMessage = new UserLeft(client.getLogin(), new Date());
        saveMessageInQueue(logoutMessage);
        broadcastMessage(logoutMessage);
        broadcastMessage(new UserList(prepareUserList()));
    }

    private List<String> prepareUserList() {
        return users.stream().map(Client::getLogin).collect(Collectors.toList());
    }

    void saveMessageInQueue(TextMessage message) {
        recentHistory.add(message);
        entireHistory.get().add(message);
        if (entireHistory.get().size() >= ENTIRE_HISTORY_SIZE) {
            flushHistory();
        }
    }

    void flushHistory() {
        List<Message> history = entireHistory.getAndSet(new CopyOnWriteArrayList<>());
        writeHistory(history, this.getName());
    }

    private void sendRecentMessages(Client client) {
        recentHistory.forEach(client::sendMessage);
    }

    String getName() {
        return name;
    }

    void closeClientConnections() {
        users.forEach(Client::closeConnection);
        flushHistory();
    }
}
