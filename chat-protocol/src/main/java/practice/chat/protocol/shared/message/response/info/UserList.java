package practice.chat.protocol.shared.message.response.info;

import practice.chat.protocol.shared.message.Message;

import java.util.ArrayList;

/**
 * Created by misha on 20.05.16.
 */
public class UserList  implements Message {

    private ArrayList<String> userList;

    public UserList(ArrayList<String> userList) {
        this.userList = userList;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }
}
