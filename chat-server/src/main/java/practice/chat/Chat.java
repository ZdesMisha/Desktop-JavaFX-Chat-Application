package practice.chat;

import practice.chat.protocol.shared.message.Login;
import practice.chat.protocol.shared.message.Logout;
import practice.chat.protocol.shared.message.MessageTemplate;
import practice.chat.protocol.shared.message.TextMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by misha on 06.05.16.
 */
public class Chat {

    Map<String, ArrayList<Client>> chatUsers = new ConcurrentHashMap<String, ArrayList<Client>>();
    ArrayList<Client> roomUsers = new ArrayList<Client>();
    Queue<MessageTemplate> messageQueue = new ConcurrentLinkedQueue<MessageTemplate>();

    public Chat() {
        chatUsers.put("defaultRoom", roomUsers);
    }

    private void broadcastMessage(MessageTemplate messageTemplate) {
        for (String room : chatUsers.keySet()) {
            for (Client client : chatUsers.get(room)) {
                client.sendMessage(messageTemplate);
            }
        }
    }

    private MessageTemplate getMessageFromQueue() {
        while (messageQueue.isEmpty()) {
        }
        return messageQueue.peek();
    }

    public void addUser(Socket socket) {
        Client client = new Client(socket);
        chatUsers.get("defaultRoom").add(client);
        client.start();

    }

    public void removeUser(Client client) {
        chatUsers.get("defaultRoom").remove(client);

    }

    private class Client extends Thread {


        private Socket socket;
        private String name = null;
        //private ChatRoom chatRoom;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        Client(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {

            MessageTemplate messageTemplate;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Socket connected " + socket);
                while (name == null) {
                    messageTemplate = (MessageTemplate) in.readObject();
                    name = messageTemplate.getLogin();
                }

                broadcastMessage(new Login(name));
                System.out.println("Connected " + name);

                while (true) {
                    messageTemplate = (MessageTemplate) in.readObject();
                    if (messageTemplate == null)
                        break;
                    broadcastMessage(messageTemplate);
                }
                System.out.println("Disconnected " + name);
                broadcastMessage(new Logout(name));

            } catch (Exception ex) {
                System.out.println("Disconnected");
                ex.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void closeConnection() {
            try {
                chatUsers.get("defaultRoom").remove(this);
                in.close();
                out.close();
                socket.close();
            } catch (Exception ex) {
                System.out.println("Cant close connection/streams");
                ex.printStackTrace();
            }
        }


        public void sendMessage(MessageTemplate messageTemplate) {
            try {
                out.writeObject(messageTemplate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
