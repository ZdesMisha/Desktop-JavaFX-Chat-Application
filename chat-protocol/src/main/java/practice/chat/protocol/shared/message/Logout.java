package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class Logout extends MessageTemplate{

    public Logout(String login) {
        this.login=login;
        message = login + " has left chat.";
    }

}
