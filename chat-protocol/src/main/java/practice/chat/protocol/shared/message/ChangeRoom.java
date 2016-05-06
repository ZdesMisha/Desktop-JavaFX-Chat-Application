package practice.chat.protocol.shared.message;

import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class ChangeRoom extends MessageTemplate {
    public ChangeRoom(String login) {
        this.login=login;
        message = new Date() + "   " + login + " has left chat room.";
    }

}
