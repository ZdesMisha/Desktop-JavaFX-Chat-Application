package practice.chat.backend;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.text.RoomClosed;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by misha on 06.05.16.
 */
public class Chat {

    private static Chat chat;

    public synchronized static Chat getInstance() {
        if (chat == null) {
            chat = new Chat();
        }
        return chat;
    }



    public final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public final Set<String> chatUsers = new HashSet<>();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom", this));
    }


    public void addToMainRoom(Client client) {
        synchronized (chatUsers) {
            if (!chatUsers.add(client.getLogin())) {
                client.HowToNameThisMethod();
            } else {
                rooms.get("MainRoom").addUser(client);
            }
        }
    }

    public void addUserToChat(Socket socket) {
        Client client = new Client(socket);
        client.start();
    }

    public void removeUserFromChat(Client client) { //TODO synch
        synchronized (chatUsers) {
            chatUsers.remove(client.getName());
        }
        client.getRoom().removeUser(client);
    }

    public void stop() {
        rooms.values().forEach(Room::closeClientConnections);
    }

    public void createNewRoom(Client client) {
        Room newRoom = new Room(this); //TODO must be synch!
        rooms.put(newRoom.getName(), newRoom);
        changeRoom(client, newRoom);
        broadcastChatMessage(new RoomList(prepareRoomList()));
    }

    private void closeRoom(Client client, Room room) {

        if (room.isMainRoom()) {
            return;
        }
        rooms.remove(room.getName());
        RoomClosed roomClosedMessage = new RoomClosed(client.getLogin(), room.getName(), new Date());
        for (Room r : rooms.values()) {
            r.saveMessageInQueue(roomClosedMessage);
        }
        broadcastChatMessage(roomClosedMessage);
        broadcastChatMessage(new RoomList(prepareRoomList()));
        room.saveHistory();

    }

    public void changeRoom(Client client, Room newRoom) { //TODO refactor synch block
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

    public void broadcastChatMessage(Message message) {
        for (Room room : rooms.values()) {
            room.broadcastMessage(message);
        }
    }

    public ArrayList<String> prepareRoomList() {
        return new ArrayList<>(rooms.keySet());
    }
}
