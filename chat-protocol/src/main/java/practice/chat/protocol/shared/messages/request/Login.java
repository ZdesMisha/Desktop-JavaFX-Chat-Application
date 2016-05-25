package practice.chat.protocol.shared.messages.request;

import practice.chat.protocol.shared.messages.Message;

/**
 * Created by misha on 20.05.16.
 */
public class Login implements Message {


    String login;

    public Login(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
