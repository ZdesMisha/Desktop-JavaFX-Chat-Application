package practice.chat.protocol.shared.messages.response.info;

import practice.chat.protocol.shared.messages.Message;

import java.util.List;

/**
 * Created by misha on 20.05.16.
 */
public class RoomList  implements Message {

    private List<String> roomList;

    public RoomList(List<String> roomList) {
        this.roomList = roomList;
    }

    public List<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<String> roomList) {
        this.roomList = roomList;
    }
}
