package practice.chat.protocol.shared.message.common;


/**
 * Created by misha on 05.05.16.
 */
public class Text extends TextMessage {

    public Text(String login, String message) {
        this.login = login;
        this.message =message;
    }

    public Text(String message) {
        super(message);
    }
}
