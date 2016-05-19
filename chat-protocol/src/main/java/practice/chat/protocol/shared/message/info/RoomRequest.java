package practice.chat.protocol.shared.message.info;

import practice.chat.protocol.shared.message.Message;

/**
 * Created by misha on 16.05.16.
 */
public class RoomRequest implements Message {
    String roomName;

    public RoomRequest() {
    }

    public RoomRequest(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


}
