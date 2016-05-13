package practice.chat.protocol.shared.message;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class MessageImpl implements Message {
    String login;
    String message;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    String date = sdf.format(new Date());

    public MessageImpl() {
    }

    public MessageImpl(String message) {
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
