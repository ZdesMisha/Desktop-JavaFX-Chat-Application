package practice.chat.protocol.shared.messages.response.text;

import practice.chat.protocol.shared.messages.TextMessage;

import java.util.Date;

import static java.lang.String.format;

/**
 * Created by misha on 20.05.16.
 */
public class RoomClosed extends TextMessage {

    public RoomClosed(String login, String roomName, Date date) {
        super(login, roomName + " closed", date);
    }
}
