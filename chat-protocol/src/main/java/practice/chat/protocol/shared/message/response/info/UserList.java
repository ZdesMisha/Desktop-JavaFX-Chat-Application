package practice.chat.protocol.shared.message.response.info;

import practice.chat.protocol.shared.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misha on 20.05.16.
 */
public class UserList  implements Message {

    private List<String> userList;

    public UserList(List<String> userList) {
        this.userList = userList;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
