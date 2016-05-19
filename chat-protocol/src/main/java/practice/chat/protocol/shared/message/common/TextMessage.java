package practice.chat.protocol.shared.message.common;


import practice.chat.protocol.shared.message.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by misha on 05.05.16.
 */
public class TextMessage implements Message {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    protected String login;
    protected String message;
    private String date;

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
        this.date = DATE_FORMAT.format(date);
    }

    @Override
    public String toString() {
        return "[" + date + "] " + login + ": " + message;
    }

}
