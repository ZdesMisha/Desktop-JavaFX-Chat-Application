package practice.chat.protocol.shared.messages.response.text;

import practice.chat.protocol.shared.messages.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class RoomCreated extends TextMessage {

    public RoomCreated(String login, String roomName, Date date) {
        super(login, "has created new room: " + roomName, date);
    }
}
