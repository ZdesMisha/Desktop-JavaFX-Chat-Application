package practice.chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by misha on 04.05.16.
 */
public class Server {

    private int port = 1234; //TODO move to config
    private boolean running = false;


    private void start() {
        running = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Chat chat = new Chat();
            System.out.println("Chat server started successfully"); //TODO change to proper logger
            while (running) {
                Socket clientSocket = serverSocket.accept();
                chat.addUser(clientSocket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stop() {
        running = false;
    }

    public static void main(String[] args) {
        final Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.stop();
            }
        }));
        server.start();
    }
}
