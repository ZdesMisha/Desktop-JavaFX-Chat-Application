package practice.chat.server;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.RoomList;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public final Map<String, Room> rooms = new HashMap<>();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom", this));
    }

    public void addToMainRoom(Socket socket) {
        synchronized (rooms) {
            rooms.get("MainRoom").addUserToMainRoom(socket);
        }
    }

    public void stop() {
        synchronized (rooms) {
            for (Room room : rooms.values()) {
                room.closeClientConnections();
            }
        }
    }

    public void createNewRoom(Client client) {
        Room newRoom = new Room(this);
        synchronized (rooms) {
            rooms.put(newRoom.getName(), newRoom);
        }
        changeRoom(client, newRoom);
    }

    public void removeRoom(Room room) {
        String roomName = room.getName();
        synchronized (rooms) {
            if (rooms.get(roomName).isMainRoom()) {
                return;
            }
            rooms.remove(roomName);
        }
    }

    public void changeRoom(Client client, Room newRoom) {
        Room oldRoom = client.getRoom();
        oldRoom.removeUser(client);
        newRoom.addUser(client);
        synchronized (rooms) {
            if (rooms.get(oldRoom.getName()).isEmptyRoom()) {
                removeRoom(oldRoom);
                broadcastChatMessage(new RoomList(prepareRoomList()));
            }
        }
    }

    public void broadcastChatMessage(Message message) {
        synchronized (rooms) {
            for (Room room : rooms.values()) {
                room.broadcastRoomMessage(message);
            }
        }
    }

    public ArrayList<String> prepareRoomList() {
        synchronized (rooms) {
            Set<String> roomSet = rooms.keySet();//TODO do something with it
            return new ArrayList<>(roomSet);
        }
    }
}
