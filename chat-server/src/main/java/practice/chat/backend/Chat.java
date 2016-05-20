package practice.chat.backend;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.response.info.RoomList;
import practice.chat.protocol.shared.message.response.text.RoomClosed;
import practice.chat.protocol.shared.message.response.text.RoomCreated;

import java.net.Socket;
import java.util.*;

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


    public void addToMainRoom(Client client) {
        synchronized (rooms) { // TODO implement user name validation
            rooms.get("MainRoom").addUser(client);
        }
    }

    public void addUserToChat(Socket socket) {
        Client client = new Client(socket);
//        client.onInit(() -> {
//            sendRecentMessages(client);
//            client.sendMessage(new RoomRequest("MainRoom"));
//        });
        client.start();
    }

    public void stop() {
        synchronized (rooms) {
            rooms.values().forEach(Room::closeClientConnections);
        }
    }

    public void createNewRoom(Client client) {
        Room newRoom = new Room(this);
        synchronized (rooms) {
            rooms.put(newRoom.getName(), newRoom);
        }
        broadcastChatMessage(new RoomList(prepareRoomList()));
        RoomCreated newRoomMessage = new RoomCreated(client.getLogin(),newRoom.getName(),new Date());
        broadcastChatMessage(newRoomMessage);
        for(Room r : rooms.values()){
            r.saveMessageInQueue(newRoomMessage);
        }
        changeRoom(client, newRoom);
    }

    private void removeRoom(Room room) {
        synchronized (rooms) {
            if (room.isMainRoom()) {
                return;
            } else {
                rooms.remove(room.getName());
                RoomClosed roomClosedMessage = new RoomClosed(room.getName(),new Date());
                broadcastChatMessage(roomClosedMessage);
                for(Room r : rooms.values()){
                    r.saveMessageInQueue(roomClosedMessage);
                }
                broadcastChatMessage(new RoomList(prepareRoomList()));
            }
        }
    }

    public void changeRoom(Client client, Room newRoom) {
        Room oldRoom = client.getRoom();
        if (oldRoom.equals(newRoom)) {
            System.out.println("Equals!");
            return;
        }
        oldRoom.removeUser(client);
        newRoom.addUser(client);
        synchronized (rooms) {
            if (oldRoom.isEmptyRoom()) {
                removeRoom(oldRoom);
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
            return new ArrayList<>(rooms.keySet());
        }
    }
}
