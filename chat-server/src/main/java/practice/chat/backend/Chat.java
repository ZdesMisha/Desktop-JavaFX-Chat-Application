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
    public final Set<String> chatUsers = new HashSet<>();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom", this));
    }


    public void addToMainRoom(Client client) {
        // TODO synch by chatUsers too
        synchronized (rooms) {  // no need
            if (chatUsers.contains(client.getLogin())) {
                client.HowToNameThisMethod();
            } else {
                chatUsers.add(client.getLogin());
                rooms.get("MainRoom").addUser(client);
            }
        }
    }

    public void addUserToChat(Socket socket) {
        Client client = new Client(socket);
        client.start();
    }

    public void removeUserFromChat(Client client) { //TODO synch
        chatUsers.remove(client.getName());
        client.getRoom().removeUser(client); //TODO looks not well
    }

    public void stop() {
        synchronized (rooms) {  // being changed
            rooms.values().forEach(Room::closeClientConnections);
        }
    }

    public void createNewRoom(Client client) {
        Room newRoom = new Room(this);
        synchronized (rooms) { // being changed
            rooms.put(newRoom.getName(), newRoom);
            changeRoom(client, newRoom);

            broadcastChatMessage(new RoomList(prepareRoomList()));
        }
    }

    private void closeRoom(Client client, Room room) {

        if (room.isMainRoom()) {
            return;
        }

        synchronized (rooms) { // change
            rooms.remove(room.getName());
            RoomClosed roomClosedMessage = new RoomClosed(client.getLogin(), room.getName(), new Date());
            broadcastChatMessage(roomClosedMessage);
            for (Room r : rooms.values()) {
                r.saveMessageInQueue(roomClosedMessage);
            }
            broadcastChatMessage(new RoomList(prepareRoomList()));
            room.saveHistory();

        }
    }

    public void changeRoom(Client client, Room newRoom) { //TODO refactor synch block
        if (newRoom == null) {
            newRoom = client.getRoom();
        }
        Room oldRoom = client.getRoom();
        oldRoom.removeUser(client);
        newRoom.addUser(client);
        if (oldRoom.isEmpty()) {
            closeRoom(client, oldRoom);
        }
    }

    public void broadcastChatMessage(Message message) {
        synchronized (rooms) { // no changes
            for (Room room : rooms.values()) {
                room.broadcastMessage(message);
            }
        }
    }

    public ArrayList<String> prepareRoomList() {
        synchronized (rooms) { // no need
            return new ArrayList<>(rooms.keySet());
        }
    }
}
