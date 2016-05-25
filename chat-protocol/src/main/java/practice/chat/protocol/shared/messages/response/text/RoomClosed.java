package practice.chat.protocol.shared.messages.response.text;

import practice.chat.protocol.shared.messages.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class RoomClosed extends TextMessage {
    String roomName;

    public RoomClosed(String login,String roomName, Date date) {
        this.login=login;
        this.roomName=roomName;
        this.message ="was closed by";
        setDate(date);
    }
    @Override
    public String toString() {
        return "[" + date + "] " + roomName + " " + message+" "+login;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}
