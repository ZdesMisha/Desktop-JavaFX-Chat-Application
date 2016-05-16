package practice.chat;

import practice.chat.protocol.shared.message.Message;
import practice.chat.protocol.shared.message.OnlineUserList;
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

    public Map<String, Room> rooms = new HashMap<>();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom",this));
    }

    public void addToMainRoom(Socket socket) {
        rooms.get("MainRoom").addUserToMainRoom(socket);
    }

    public void stop() { //TODO impementation
    }

    public void createNewRoom(Client client) {
        Room newRoom = new Room(this);
        rooms.put(newRoom.getName(), newRoom);
        changeRoom(client, newRoom);
    }

    public void removeRoom(Room room) {
        String roomName = room.getName();
        if (rooms.get(roomName).isMainRoom()) {
            return;
        }
        rooms.remove(roomName);
    }

    public void changeRoom(Client client, Room newRoom) {
        Room oldRoom = client.getRoom();
        oldRoom.removeUser(client);
        newRoom.addUser(client);
        if (rooms.get(oldRoom.getName()).isEmptyRoom()) {
            removeRoom(oldRoom);
            broadcastChatMessage(new RoomList(prepareRoomList()));
        } else {
            oldRoom.broadcastRoomMessage(new RoomList(prepareRoomList()));
        }
    }

    public void broadcastChatMessage(Message message) {
        for (Room room : rooms.values()) {
            room.broadcastRoomMessage(message);
        }
    }

    public ArrayList<String> prepareRoomList() {
        Set<String> roomSet = rooms.keySet();//TODO do something with it
        return new ArrayList<>(roomSet);
    }
}
