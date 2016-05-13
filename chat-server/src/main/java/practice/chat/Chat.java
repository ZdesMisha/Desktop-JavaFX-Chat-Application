package practice.chat;

import practice.chat.protocol.shared.message.Message;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by misha on 06.05.16.
 */
public class Chat {

    public Map<String, Room> rooms = new HashMap<>();

    public Chat() {
        rooms.put("MainRoom", new Room("MainRoom",this));
    }

    public void addUserToMainRoom(Socket socket) {
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
        if (rooms.get(room.getName()).isMainRoom()) {
            return;
        }
        rooms.remove(room.getName());
    }

    public void changeRoom(Client client, Room room) {
        client.getRoom().removeUser(client);
        if (rooms.get(room.getName()).isEmptyRoom(client.getRoom())) {
            removeRoom(client.getRoom());
        }
        client.setRoom(room);
        rooms.get(room.getName()).addUser(client);
    }
    public void sendGlobalMessage(Message message){
        for(Room room : rooms.values()){
            room.broadcastMessage(message);
        }
    }


}
