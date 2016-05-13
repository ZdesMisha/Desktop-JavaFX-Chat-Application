package practice.chat.protocol.shared.persistence;


/**
 * Created by misha on 04.05.16.
 */
public class ChatRoom {

    private boolean mainRoomFlag;
    private static int roomCounter=1;
    String name;
    int roomId;
    private int userCounter;
    //private ArrayList<Chat.Client> users = new ArrayList<>();

    public ChatRoom(){
        this.name = "Room " + roomCounter++;
        mainRoomFlag =false;
    }

    public ChatRoom(String name){
        this.name=name;
        roomCounter++;
        mainRoomFlag =true;
    }

//    public void addUser(Chat.Client user){
//        users.add(user);
//    }
//
//    public void removeUser(Chat.Client user){
//        users.remove(user);
//    }

    public int getUserCounter() {
        return userCounter;
    }

    public void setUserCounter(int userCounter) {
        this.userCounter = userCounter;
    }

    public String getName() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public boolean isMainRoomFlag(){
        return mainRoomFlag;
    }
//    public ArrayList<Chat.Client> getUsers() {
//        return users;
//    }
//
//    public void setUsers(ArrayList<Chat.Client> users) {
//        this.users = users;
//    }
}
