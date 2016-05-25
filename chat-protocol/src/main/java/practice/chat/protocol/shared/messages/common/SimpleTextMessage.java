package practice.chat.protocol.shared.messages.common;

import practice.chat.protocol.shared.messages.TextMessage;

/**
 * Created by misha on 20.05.16.
 */
public class SimpleTextMessage extends TextMessage {
    public SimpleTextMessage(String login, String message) {
        this.login=login;
        this.message = message;
    }
}
