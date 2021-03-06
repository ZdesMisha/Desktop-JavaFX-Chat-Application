package practice.chat.backend;

import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.messages.response.info.RoomList;
import practice.chat.protocol.shared.messages.response.info.ViolatedLoginUniqueConstraint;
import practice.chat.protocol.shared.messages.response.text.RoomClosed;
import practice.chat.protocol.shared.messages.response.text.RoomCreated;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by misha on 06.05.16.
 */
public final class Chat {

    private static volatile Chat chat;
    private final Server server;
    private final AtomicInteger roomCounter = new AtomicInteger(0);
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Set<String> chatUsers = ConcurrentHashMap.newKeySet();

    private Chat(Server server) {
        this.server = server;
        rooms.put("MainRoom", new Room("MainRoom", this));
    }

    static Chat initInstance(Server server) {
        if (chat == null) {
            synchronized (Chat.class) {
                if (chat == null) {
                    chat = new Chat(server);
                }
            }
        }
        return chat;
    }

    static Chat getInstance() {
        if (chat == null) {
            throw new IllegalStateException("Chat is not initialized");
        }
        return chat;
    }

    void addUserToChat(Socket socket) {
        Client client = new Client(socket);
        client.start();
    }

    boolean isLoginOccupied(String login) {
        return chatUsers.contains(login);
    }

    void addToMainRoom(Client client) {
        String login = client.getLogin();
        chatUsers.add(login);
        rooms.get("MainRoom").addUser(client);
    }

    void removeUserFromChat(Client client) {
        server.decreaseConnectionsCounter();
        String login = client.getLogin();
        Room currentRoom = client.getRoom();
        if (currentRoom != null && login != null) {
            chatUsers.remove(client.getLogin());
            currentRoom.removeUser(client);
            synchronized (rooms) {
                if (currentRoom.isEmpty()) {
                    closeRoom(client, currentRoom);
                }
            }
        }
    }

    void createNewRoom(Client client) {
        Room newRoom = new Room(roomCounter.incrementAndGet(), chat);
        rooms.put(newRoom.getName(), newRoom);
        changeRoom(client, newRoom.getName());
        RoomCreated roomCreatedMessage = new RoomCreated(client.getLogin(), newRoom.getName(), new Date());
        rooms.values().forEach(r -> r.saveMessageInQueue(roomCreatedMessage));
        broadcastChatMessage(roomCreatedMessage);
        broadcastChatMessage(new RoomList(prepareRoomList()));
    }

    void changeRoom(Client client, String room) {
        synchronized (rooms) {
            Room newRoom = rooms.get(room);
            Room oldRoom = client.getRoom();
            if (newRoom == null) {
                newRoom = oldRoom;
            }
            oldRoom.removeUser(client);
            newRoom.addUser(client);
            if (oldRoom.isEmpty()) {
                closeRoom(client, oldRoom);
            }
        }
    }

    private void closeRoom(Client client, Room room) {
        if (room.isMainRoom()) {
            return;
        }
        rooms.remove(room.getName());
        RoomClosed roomClosedMessage = new RoomClosed(client.getLogin(), room.getName(), new Date());
        rooms.values().forEach(r -> r.saveMessageInQueue(roomClosedMessage));
        broadcastChatMessage(roomClosedMessage);
        broadcastChatMessage(new RoomList(prepareRoomList()));
        room.flushHistory();
    }

    private void broadcastChatMessage(Message message) {
        rooms.values().forEach(r -> r.broadcastMessage(message));
    }

    List<String> prepareRoomList() {
        return new ArrayList<>(rooms.keySet());
    }

    void stop() {
        rooms.values().forEach(Room::closeClientConnections);
        chat = null;
    }
}
