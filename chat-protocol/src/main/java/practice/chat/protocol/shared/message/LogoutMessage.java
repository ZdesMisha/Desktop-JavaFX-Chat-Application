package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class LogoutMessage extends MessageImpl {

    public LogoutMessage(String login) {
        this.login=login;
        message = "["+date+"] "+login + " has left chat.";
    }

}
