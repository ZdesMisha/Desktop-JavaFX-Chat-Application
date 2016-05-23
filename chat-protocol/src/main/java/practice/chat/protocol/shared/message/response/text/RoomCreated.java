package practice.chat.protocol.shared.message.response.text;

import practice.chat.protocol.shared.message.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class RoomCreated extends TextMessage {
    String roomName;

    public RoomCreated(String login,String roomName,Date date) {
        this.login=login;
        this.roomName = roomName;
        this.message = "has created new room: " + roomName;
        setDate(date);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
