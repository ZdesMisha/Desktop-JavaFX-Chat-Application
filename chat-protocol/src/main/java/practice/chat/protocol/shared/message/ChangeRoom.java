package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class ChangeRoom extends MessageImplementation {

    private String room;

    public ChangeRoom(String login, String room) {
        this.login = login;
        this.room = room;
        this.message =login + " has left";
    }

    public String getRoom() {
        return room;
    }
}
