package practice.chat.protocol.shared.message.example.response.info;

import practice.chat.protocol.shared.message.example.Mess;

import java.util.ArrayList;

/**
 * Created by misha on 20.05.16.
 */
public class RoomList  implements Mess {

    private ArrayList<String> roomList;

    public RoomList(ArrayList<String> roomList) {
        this.roomList = roomList;
    }

    public ArrayList<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<String> roomList) {
        this.roomList = roomList;
    }
}
