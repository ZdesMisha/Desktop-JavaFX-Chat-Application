package practice.chat.protocol.shared.message.request;

import practice.chat.protocol.shared.message.Message;

/**
 * Created by misha on 20.05.16.
 */
public class CreateRoom implements Message {
    String login;

    public CreateRoom(String login) {
        this.login = login;
    }
}
