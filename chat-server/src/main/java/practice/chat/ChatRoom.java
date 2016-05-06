package practice.chat;

import practice.chat.protocol.shared.message.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misha on 04.05.16.
 */
public class ChatRoom {

    int roomNumber;
    List<TextMessage> messageList = new ArrayList<TextMessage>();

    public ChatRoom(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
