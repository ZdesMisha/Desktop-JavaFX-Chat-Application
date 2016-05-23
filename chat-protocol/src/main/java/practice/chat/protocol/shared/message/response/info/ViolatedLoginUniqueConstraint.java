package practice.chat.protocol.shared.message.response.info;

import practice.chat.protocol.shared.message.Message;

/**
 * Created by misha on 23.05.16.
 */
public class ViolatedLoginUniqueConstraint implements Message{
    String login;
    public ViolatedLoginUniqueConstraint(String login){
        this.login=login;
    }
}
