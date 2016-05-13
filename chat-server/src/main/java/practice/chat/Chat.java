package practice.chat;

import practice.chat.protocol.shared.message.*;
import practice.chat.protocol.shared.message.LoginMessage;
import practice.chat.protocol.shared.message.LogoutMessage;

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

    public Chat() {
        chatUsers.put("defaultRoom", new ArrayList<Client>());
    }

    private synchronized void broadcastMessage(Message message) {
        for (String room : chatUsers.keySet()) {
            for (Client client : chatUsers.get(room)) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void addUser(Socket socket) {
        Client client = new Client(socket);
        chatUsers.get("defaultRoom").add(client);
        client.start();
    }

    public synchronized void removeUser(Client client) {
        chatUsers.get("defaultRoom").remove(client);
    }

    public synchronized void stop() {
        for (String room : chatUsers.keySet()) {
            chatUsers.get(room).forEach(Client::closeConnection);
        }
    }

    private class Client extends Thread {

        private Socket socket;
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
            broadcastMessage(new WhoIsOnlineMessage(prepareUserList()));
            broadcastMessage(message);
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

        public void sendMessage(Message message){ //TODO how to handle send message error exception
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
    }
}
