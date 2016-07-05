package practice.chat.protocol.shared.messages.common;

import practice.chat.protocol.shared.messages.TextMessage;

/**
 * Created by misha on 20.05.16.
 */
public class UserTextMessage extends TextMessage {

    public UserTextMessage(String login, String message) {
        super(login, message);
    }

}
