package practice.chat.protocol.shared.messages.response.info;

import practice.chat.protocol.shared.messages.Message;

/**
 * Created by misha on 20.05.16.
 */
public class CurrentRoom implements Message {

    private String roomName;

    public CurrentRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

}
