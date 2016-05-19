package practice.chat.protocol.shared.message.common;

/**
 * Created by misha on 16.05.16.
 */
public class Logout extends TextMessage {

    public Logout(String login) {
        this.login = login;
        this.message = "has left";
    }

}
