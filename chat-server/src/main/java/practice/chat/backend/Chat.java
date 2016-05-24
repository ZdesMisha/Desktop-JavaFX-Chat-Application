package practice.chat.backend;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.info.ViolatedLoginUniqueConstraint;
import practice.chat.protocol.shared.message.response.text.RoomClosed;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by misha on 06.05.16.
 */
public class Chat {

    private static volatile Chat chat;
    private AtomicInteger roomCounter = new AtomicInteger(0);
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Set<String> chatUsers = ConcurrentHashMap.newKeySet();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom", this));
    }

    public static Chat getInstance() {
        if (chat == null) {
            synchronized (Chat.class) {
                if (chat == null) {
                    chat = new Chat();
                }
            }
        }
        return chat;
    }

    private Room getNewRoom() {
        return new Room(roomCounter.incrementAndGet(), chat);
    }

    public void addUserToChat(Socket socket) {
        Client client = new Client(socket);
        client.start();
    }

    public void addToMainRoom(Client client) {
        String login = client.getLogin();
        if (chatUsers.add(login)) {
            rooms.get("MainRoom").addUser(client);
        } else {
            client.sendMessage(new ViolatedLoginUniqueConstraint(login));
        }
    }

    public void removeUserFromChat(Client client) { //TODO synch ??
        chatUsers.remove(client.getName());
        Room currentRoom = client.getRoom();
        currentRoom.removeUser(client);
        synchronized (rooms) {
            if (currentRoom.isEmpty()) {
                closeRoom(client, currentRoom);
            }
        }
    }

    public void createNewRoom(Client client) {
        Room newRoom = getNewRoom();
        rooms.put(newRoom.getName(), newRoom);
        changeRoom(client, newRoom.getName());
        broadcastChatMessage(new RoomList(prepareRoomList()));
    }

    public void changeRoom(Client client, String room) {
        Room oldRoom;
        Room newRoom = rooms.get(room);
        oldRoom = client.getRoom();
        if (newRoom == null) {
            newRoom = oldRoom;
        }
        oldRoom.removeUser(client);
        newRoom.addUser(client);
        synchronized (rooms) {
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
        room.saveHistory();
    }

    public void broadcastChatMessage(Message message) {
        rooms.values().forEach(r -> r.broadcastMessage(message));
    }

    public List<String> prepareRoomList() {
        return new ArrayList<>(rooms.keySet());
    }

    public void stop() {
        rooms.values().forEach(Room::closeClientConnections);
    }
}
