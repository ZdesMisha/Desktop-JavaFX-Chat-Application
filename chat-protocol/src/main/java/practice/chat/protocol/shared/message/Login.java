package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class Login extends MessageTemplate{

    public Login(String login) {
        this.login = login;
        message = login + " has joined chat.";
    }

}
