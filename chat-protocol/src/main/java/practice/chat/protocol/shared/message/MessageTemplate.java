package practice.chat.protocol.shared.message;

import java.io.Serializable;

/**
 * Created by misha on 05.05.16.
 */
public class MessageTemplate implements Serializable {
    String login;
    String message;

    public MessageTemplate() {
    }

    public MessageTemplate(String message) {
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
