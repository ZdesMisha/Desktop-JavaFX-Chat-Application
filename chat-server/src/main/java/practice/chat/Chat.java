package practice.chat;

import practice.chat.protocol.shared.message.Login;
import practice.chat.protocol.shared.message.Logout;
import practice.chat.protocol.shared.message.MessageTemplate;

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

    private void broadcastMessage(MessageTemplate messageTemplate) {
        for (String room : chatUsers.keySet()) {
            for (Client client : chatUsers.get(room)) {
                client.sendMessage(messageTemplate);
            }
        }
    }

    public void addUser(Socket socket) {
        Client client = new Client(socket);
        chatUsers.get("defaultRoom").add(client);
        client.start();

    }

    public void removeUser(Client client) {
        chatUsers.get("defaultRoom").remove(client);

    }

    public void stop() {
        for (String room : chatUsers.keySet()) {
            for (Client client : chatUsers.get(room)) {
                client.closeConnection();
            }
        }
    }

    private class Client extends Thread {


        private Socket socket;
        private String name = null;
        private ObjectOutputStream out;
        private ObjectInputStream in;


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

                while (true) {
                    messageTemplate = (MessageTemplate) in.readObject();
                    if (messageTemplate == null)
                        break;
                    broadcastMessage(messageTemplate);
                }

            } catch (Exception ex) {
                System.out.println("Exception in Client run method");
                ex.printStackTrace();
            } finally {
                broadcastMessage(new Logout(name));
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
