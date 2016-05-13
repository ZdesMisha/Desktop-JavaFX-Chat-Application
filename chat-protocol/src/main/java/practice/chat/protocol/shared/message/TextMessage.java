package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class TextMessage extends MessageImpl {

    public TextMessage(String login, String message) {
        this.login=login;
        this.message = "["+date+"] "+login + ": " + message;
    }
    public TextMessage(String message){
        super(message);
    }
}
