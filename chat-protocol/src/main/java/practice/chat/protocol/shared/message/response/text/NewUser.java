package practice.chat.protocol.shared.message.response.text;

import practice.chat.protocol.shared.message.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class NewUser extends TextMessage {

    public NewUser(String login,Date date) {
        this.login = login;
        this.message = "has joined chat";
        setDate(date);
    }
}
