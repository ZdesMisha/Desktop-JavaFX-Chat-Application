package practice.chat.protocol.shared.message.common;

/**
 * Created by misha on 05.05.16.
 */
public class Login extends TextMessage {

    public Login(String login) {
        this.login = login;
        this.message = "has joined chat";
    }

}
