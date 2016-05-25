package practice.chat.protocol.shared.messages;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by misha on 20.05.16.
 */
public class TextMessage implements Message {

    private static final String DATE_FORMAT = "HH:mm:ss dd/MM/yyyy";

    protected String login;
    protected String message;
    protected String date;

    public TextMessage() {
    }

    public TextMessage(String message) {
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

    public void setDate(Date date) {
        this.date = new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    @Override
    public String toString() {
        return "[" + date + "] " + login + ": " + message;
    }
}
