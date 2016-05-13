package practice.chat;

import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.LoginMessage;
import practice.chat.protocol.shared.message.LogoutMessage;
import practice.chat.protocol.shared.persistence.ChatRoom;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by misha on 06.05.16.
 */
public class Chat {

    private Map<String, ArrayList<Client>> chatUsers = new HashMap<String, ArrayList<Client>>();
    //private ArrayList<ChatRoom> chatRooms= new ArrayList<ChatRoom>;

    public Chat() {
        chatUsers.put(new ChatRoom("MainRoom").getName(), new ArrayList<Client>());
    }

    private synchronized void broadcastMessage(Message message,ChatRoom room) {
            for (Client client : chatUsers.get(room.getName())) {
                client.sendMessage(message);
            }
    }

    public synchronized void addUser(Socket socket) {
        Client client = new Client(socket);
        chatUsers.get("MainRoom").add(client);
        client.start();
    }

    public synchronized void removeUser(Client client) {
        chatUsers.get("MainRoom").remove(client);
    }

    public synchronized void stop() {
        for (String room : chatUsers.keySet()) {
            chatUsers.get(room).forEach(Client::closeConnection);
        }
    }

    public void createNewRoom(Client client) {
        ChatRoom newRoom = new ChatRoom();
        chatUsers.put(newRoom.getName(), new ArrayList<>());
        moveUserToRoom(client,newRoom);
    }

    public void removeRoom(ChatRoom chatRoom) {
        if (chatRoom.isMainRoomFlag()) {
            return;
        }
        chatUsers.remove(chatRoom.getName());
    }

    public void moveUserToRoom(Client client, ChatRoom chatRoom) {
        chatUsers.get(client.getChatRoom()).remove(client);
        client.setChatRoom(chatRoom);
        chatUsers.get(chatRoom.getName()).add(client);
    }

    private class Client extends Thread {

        private Socket socket;
        private ChatRoom chatRoom;
        private String login = null;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        Client(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            Message message;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                System.out.println("Connected: " + socket); //TODO logger

                LoginMessage welcomeMessage = (LoginMessage) input.readObject();
                login = welcomeMessage.getLogin();
                processMessage(welcomeMessage);

                while (true) {
                    message = (Message) input.readObject();
                    System.out.println(message);
                    if (message == null) //TODO ????
                        break;
                    processMessage(message);
                }
            } catch (Exception ex) {
                System.out.println("Exception input Client run method"); //TODO logger
                ex.printStackTrace();
            } finally {
                closeConnection();
                System.out.println("Socket closed."); //TODO logger
                processMessage(new LogoutMessage(login));
            }
        }

        public void processMessage(Message message) {
            if (message instanceof CreateNewRoomMessage) {
                broadcastMessage(new LogoutMessage(login),chatRoom);
                broadcastMessage(new WhoIsOnlineMessage(prepareUserList()),chatRoom);
                createNewRoom(this);
                broadcastMessage(new LoginMessage(login),chatRoom);
                broadcastMessage(new WhoIsOnlineMessage(prepareUserList()),chatRoom);
            } else {
                broadcastMessage(new WhoIsOnlineMessage(prepareUserList()),chatRoom);
                broadcastMessage(message,chatRoom);
            }
        }

        private void closeConnection() {
            try {
                removeUser(this);
                output.close();
                input.close();
                socket.close();
            } catch (Exception ex) {
                System.out.println("Cant close connection/streams"); //TODO logger
                ex.printStackTrace();
            }
        }

        public void sendMessage(Message message) { //TODO how to handle send message error exception
            try {
                output.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ArrayList<String> prepareUserList() {
            ArrayList<String> userList = new ArrayList<>();
            for (String room : chatUsers.keySet()) {
                for (Client client : chatUsers.get(room)) {
                    userList.add(client.getLogin());
                }
            }
            return userList;
        }

        public String getLogin() {
            return login;
        }


        public ChatRoom getChatRoom() {
            return chatRoom;
        }

        public void setChatRoom(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }
    }
}
