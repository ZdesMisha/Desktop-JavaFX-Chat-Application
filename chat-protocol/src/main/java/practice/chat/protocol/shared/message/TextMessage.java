package practice.chat.protocol.shared.message;

import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class TextMessage extends MessageTemplate {

    public TextMessage(String login, String message) {
        this.login=login;
        this.message = new Date() + "   " + login + ": " + message;
    }
    public TextMessage(String message){
        super(message);
    }
}
