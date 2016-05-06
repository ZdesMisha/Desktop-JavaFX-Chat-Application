package practice.chat;

import practice.chat.protocol.shared.message.Login;
import practice.chat.protocol.shared.message.MessageTemplate;
import practice.chat.protocol.shared.message.TextMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Client {

    private String ip = "127.0.0.1";
    private int port = 1234;
    private InetAddress address;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private BufferedReader keyboardIn;

    private Thread keyboardListenerThread;
    private Thread serverListenerThread;


    private void start() {
        try {
            address = InetAddress.getByName(ip);
            socket = new Socket(address, port);
        }catch(Exception ex){
            System.out.println("Cant access server!");
            ex.printStackTrace();
        }

        keyboardListenerThread = new Thread(new KeyboardListener());
        keyboardListenerThread.start();

        serverListenerThread = new Thread(new ServerListener());
        serverListenerThread.start();
    }

    private void stop() {
        try {
            input.close();
            output.close();
            socket.close();
            keyboardIn.close();
        } catch (Exception ex) {
            System.out.println("Error during stopping client app");
            ex.printStackTrace();
        } finally {

            keyboardListenerThread.interrupt();
            serverListenerThread.interrupt();
        }
    }

    private class KeyboardListener implements Runnable {

        public void run() {

            try {
                keyboardIn = new BufferedReader(new InputStreamReader(System.in));
                output = new ObjectOutputStream(socket.getOutputStream());
                String line = null;

                while (line == null) {
                    System.out.println("Your name:");
                    line = keyboardIn.readLine();
                }
                output.writeObject(new Login(line));
                while (true) {
                    line = keyboardIn.readLine();
                    if (line.equals("exit")) {
                        break;
                    }
                    output.writeObject(new TextMessage(line));
                }
                stop();
            } catch (Exception ex) {
                System.out.println("Exception in keyboard listener");
                ex.printStackTrace();
            }
        }
    }

    private class ServerListener implements Runnable {

        public void run() {

            try {
                input = new ObjectInputStream(socket.getInputStream());
                MessageTemplate messageTemplate = null;
                while (true) {
                    messageTemplate = (MessageTemplate) input.readObject();
                    System.out.println(messageTemplate.getMessage());
                }
            } catch (Exception ex) {
                System.out.println("Exception in server listener");
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final Client client = new Client();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                client.stop();
            }
        }));
        client.start();
    }
}
