package practice.chat.protocol.shared.message.response.text;

import practice.chat.protocol.shared.message.TextMessage;

import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class UserLeft extends TextMessage {

    public UserLeft(String login,Date date) {
        this.login = login;
        this.message = "has left room";
        setDate(date);
    }
}
