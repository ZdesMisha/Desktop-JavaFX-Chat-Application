package practice.chat.protocol.shared.message;



/**
 * Created by misha on 05.05.16.
 */
public class MessageImplementation implements Message {
    String login;
    String message;

    public MessageImplementation() {
    }

    public MessageImplementation(String message) {
        this.message = message;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
