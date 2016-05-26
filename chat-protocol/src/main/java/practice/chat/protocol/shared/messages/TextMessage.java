package practice.chat.protocol.shared.messages;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;

/**
 * Created by misha on 20.05.16.
 */
public abstract class TextMessage implements Message {

    private static final String DATE_FORMAT = "HH:mm:ss dd/MM/yyyy";

    private String login;
    private String message;
    private Date date;

    public TextMessage(String login, String message) {
        this.login = login;
        this.message = message;
    }

    public TextMessage(String login, String message, Date date) {
        this.login = login;
        this.message = message;
        this.date = date;
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
        this.date = date;
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    @Override
    public String toString() {
        return format("[%s] %s: %s", formatDate(date), login, message);
    }

}
