package practice.chat.protocol.shared.message.common;

import practice.chat.protocol.shared.message.TextMessage;

/**
 * Created by misha on 20.05.16.
 */
public class SimpleTextMessage extends TextMessage {
    public SimpleTextMessage(String login, String message) {
        this.login=login;
        this.message = message;
    }
}
