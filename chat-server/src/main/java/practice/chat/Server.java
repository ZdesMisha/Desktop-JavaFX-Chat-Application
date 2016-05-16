package practice.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by misha on 04.05.16.
 */
public class Server {

    private static final int PORT = 1234; //TODO move to config
    private boolean running = false;
    private Chat chat;
    private ServerSocket serverSocket;


    private void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(PORT);
            chat = Chat.getInstance();
            System.out.println("Chat server started successfully"); //TODO  logger
            while (running) {
                Socket clientSocket = serverSocket.accept();
                chat.addToMainRoom(clientSocket);
            }
        } catch (Exception ex) {
            System.out.println("Server failure"); //TODO  logger
            ex.printStackTrace();
        } finally {
            stop();
        }
    }

    private void stop() {
        try {
            System.out.println("1");
            running = false;
            chat.stop();
            System.out.println("2");

            serverSocket.close();
            System.out.println("3");

            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Can not stop Server correctly");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) { //TODO implement proper exit
        Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.stop();
            }
        }));
        server.start();
    }
}
