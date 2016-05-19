package practice.chat.server;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.common.CreateNewRoom;
import practice.chat.protocol.shared.message.common.TextMessage;
import practice.chat.protocol.shared.message.info.RoomList;

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
        CreateNewRoom newRoomMessage = new CreateNewRoom(client.getLogin());
        newRoomMessage.setRoomName(newRoom.getName()); //TODO get rid of this shit
        newRoomMessage.setDate(new Date());
        for(Room room : rooms.values()){
            room.saveMessageInQueue(newRoomMessage);
        }
        broadcastChatMessage(new CreateNewRoom(client.getLogin()));
        changeRoom(client, newRoom);
    }

    private void removeRoom(Room room) {
        synchronized (rooms) {
            if (room.isMainRoom()) {
                return;
            } else {
                rooms.remove(room.getName());
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
