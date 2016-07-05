package practice.chat.protocol.shared.messages.response.text;

import practice.chat.protocol.shared.messages.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class NewUser extends TextMessage {

    public NewUser(String login, Date date) {
        super(login, "has joined chat", date);
    }
}
