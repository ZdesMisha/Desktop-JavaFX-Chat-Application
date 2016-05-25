package practice.chat.protocol.shared.messages.response.info;

import practice.chat.protocol.shared.messages.Message;

/**
 * Created by misha on 20.05.16.
 */
public class CurrentRoom implements Message {

    String roomName;

    public CurrentRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
