package practice.chat.protocol.shared.message.response.info;

import practice.chat.protocol.shared.message.Message;

import java.util.ArrayList;

/**
 * Created by misha on 20.05.16.
 */
public class RoomList  implements Message {

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
