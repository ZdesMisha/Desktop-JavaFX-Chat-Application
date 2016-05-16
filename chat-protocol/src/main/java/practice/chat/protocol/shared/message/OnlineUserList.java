package practice.chat.protocol.shared.message;


import java.util.ArrayList;

/**
 * Created by misha on 12.05.16.
 */
public class OnlineUserList implements Message { //TODO reconsider class name

    private ArrayList<String> userList;

    public OnlineUserList(ArrayList<String> userList) {
        this.userList = userList;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }


}
