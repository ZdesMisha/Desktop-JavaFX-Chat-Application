package practice.chat.protocol.shared.message.request;

import practice.chat.protocol.shared.message.Message;

/**
 * Created by misha on 20.05.16.
 */
public class Logout implements Message {
    String login;

    public Logout(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
