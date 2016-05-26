package practice.chat.protocol.shared.messages.request;

import practice.chat.protocol.shared.messages.Message;

/**
 * Created by misha on 20.05.16.
 */
public class ChangeRoom implements Message {

    private String room;

    public ChangeRoom(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }
}
