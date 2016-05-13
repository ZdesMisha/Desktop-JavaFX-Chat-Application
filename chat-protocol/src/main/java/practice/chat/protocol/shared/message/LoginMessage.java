package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class LoginMessage extends MessageImpl {

    public LoginMessage(String login) {
        this.login = login;
        message = "["+date+"] "+login + " has joined chat.";
    }

}
