package practice.chat.background;

import practice.chat.protocol.shared.message.MessageTemplate;
import practice.chat.protocol.shared.message.TextMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Consumer;


public class Client {

    private Consumer<MessageTemplate> consumer;
    private ServerConnection serverConnection = new ServerConnection();


    public Client(Consumer<MessageTemplate> consumer) {
        this.consumer = consumer;
    }

    public void start() {
        serverConnection.start();
    }

    public void stop() {
        try {
            serverConnection.socket.close();
        }catch(IOException ex){
            System.out.println("Can't close connection");
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        serverConnection.output.writeObject(new TextMessage(message));
    }

    private class ServerConnection extends Thread {

        private ObjectOutputStream output;
        private ObjectInputStream input;
        private Socket socket;
        private String ip = "127.0.0.1";
        private int port = 1234;
        private InetAddress address;
        private MessageTemplate messageTemplate;

        public void run() {

            try {
                address = InetAddress.getByName(ip);
                socket = new Socket(address, port);
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    messageTemplate = (MessageTemplate) input.readObject();
                    consumer.accept(messageTemplate);
                }
            } catch (Exception ex) {
                consumer.accept(new TextMessage("Can not access server,please try to connect again."));
                System.out.println("Server listener failure!");
                ex.printStackTrace();
            }finally {
                try {
                    output.close();
                    input.close();
                    socket.close();
                }catch (IOException ex){
                    System.out.println("Can not close IO streams.");
                    ex.printStackTrace();
                }
            }
        }
    }


}
