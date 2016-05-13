package practice.chat.protocol.shared.message;

import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class ChangeRoomMessage extends MessageImpl {
    public ChangeRoomMessage(String login) {
        this.login=login;
        message = new Date() + "   " + login + " has left chat room.";
    }

}
