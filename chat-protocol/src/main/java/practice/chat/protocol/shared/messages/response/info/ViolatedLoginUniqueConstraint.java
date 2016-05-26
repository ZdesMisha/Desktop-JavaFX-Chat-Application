package practice.chat.protocol.shared.messages.response.info;

import practice.chat.protocol.shared.messages.Message;

/**
 * Created by misha on 23.05.16.
 */
public class ViolatedLoginUniqueConstraint implements Message {


    private String login;

    public ViolatedLoginUniqueConstraint(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
