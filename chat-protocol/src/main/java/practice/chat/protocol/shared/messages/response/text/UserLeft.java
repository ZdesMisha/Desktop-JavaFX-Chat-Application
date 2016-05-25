package practice.chat.protocol.shared.messages.response.text;

import practice.chat.protocol.shared.messages.TextMessage;

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
