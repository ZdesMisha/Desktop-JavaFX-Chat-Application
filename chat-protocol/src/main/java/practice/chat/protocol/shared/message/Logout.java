package practice.chat.protocol.shared.message;

/**
 * Created by misha on 16.05.16.
 */
public class Logout extends MessageImplementation {

    public Logout(String login) {
        this.login = login;
        this.message = login + " has left";
    }

}
