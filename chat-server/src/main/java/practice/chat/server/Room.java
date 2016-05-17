package practice.chat.server;


import practice.chat.protocol.shared.message.*;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by misha on 04.05.16.
 */
public class Room {

    private Chat chat;
    private boolean isMainRoom;
    private static int roomCounter = 1;
    private String name;
    private PrintWriter historyWriter;
    private File file;

    private List<MessageImplementation> recentHistory = new LinkedList<>();
    public ArrayList<Client> users = new ArrayList<>();


    public Room(Chat chat) {
        name = "Room_" + roomCounter++;
        this.chat = chat;
        isMainRoom = false;
    }

    public Room(String name, Chat chat) {
        this.name = name;
        this.chat = chat;
        isMainRoom = true;
    }

    @Override
    public String toString() {
        return name + " with users: " + users;
    }

    public void broadcastRoomMessage(Message message) {
        for (Client client : users) {
            client.sendMessage(message);
        }
    }

    public synchronized boolean isEmptyRoom() {
        return users.isEmpty();
    }

    public boolean isMainRoom() {
        return isMainRoom;
    }

    public synchronized void addUserToMainRoom(Socket socket) {
        Client client = new Client(socket, this);
        client.onInit(() -> sendRecentMessages(client));
        client.start();
        users.add(client);
        client.setRoom(chat.rooms.get("MainRoom"));
    }

    public synchronized void addUser(Client client) {
        users.add(client);
        client.setRoom(this);
        Message loginMessage = new TextMessage(client.getLogin() + " has joined the chat");
        broadcastRoomMessage(loginMessage);
        saveMessageInQueue(loginMessage);
        broadcastRoomMessage(new RoomList(chat.prepareRoomList()));
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
        client.sendMessage(new RoomRequest(this.getName())); //TODO may there is a better way to inform client about his current room?
    }

    public synchronized void removeUser(Client client) {
        users.remove(client);
        Message logoutMessage = new Logout(client.getLogin());
        broadcastRoomMessage(logoutMessage);
        saveMessageInQueue(logoutMessage);
        broadcastRoomMessage(new OnlineUserList(prepareUserList()));
    }

    public synchronized ArrayList<String> prepareUserList() {
        ArrayList<String> userList = new ArrayList<>();
        for (Client user : users) {
            userList.add(user.getLogin());
        }
        return userList;
    }

    public synchronized void saveMessageInQueue(Message message) {
        if (recentHistory.size() > 10) {
            saveHistoryToFile(recentHistory);
            recentHistory.clear();
        }
        recentHistory.add((MessageImplementation) message);
    }

    public synchronized void sendRecentMessages(Client client) {
        int size = recentHistory.size(); //TODO fix
        if (size > 10) {
            for (int i = 1; i < 10; i++) {
                client.sendMessage(recentHistory.get(size - i));
            }
        } else {
            for (Message message : recentHistory) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void saveHistoryToFile(List<MessageImplementation> history) {
        file = new File("/home/misha/ChatHistory/" + name + ".txt"); //TODO IMPLEMENT PROPER HISTORY DIR INSTEAD OF THIS SHIT!
        try {
            historyWriter = new PrintWriter(file);
            for (MessageImplementation message : history) {
                historyWriter.write(message.getMessage() + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            historyWriter.close();
        }
        Server.serverController.updateRoomList(chat.prepareRoomList()); ///TODO fix
    }

    public String getName() {
        return name;
    }

}
