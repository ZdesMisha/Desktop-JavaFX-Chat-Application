package practice.chat.protocol.shared.message;

import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class ChangeRoomMessage extends MessageImpl {

    private String room;

    public ChangeRoomMessage(String login, String room) {
        this.login = login;
        this.room = room;
        message = new Date() + "   " + login + " has left chat room.";
    }

}
