package practice.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by misha on 04.05.16.
 */
public class Server {

    private int port = 1234; //TODO move to config
    private boolean running = false;
    private Chat chat;
    private ServerSocket serverSocket;


    private void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            chat = new Chat();
            System.out.println("Chat server started successfully"); //TODO change to proper logger
            while (running) {
                Socket clientSocket = serverSocket.accept();
                chat.addUser(clientSocket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            stop();
        }
    }

    private void stop() {
        try {
            chat.stop();
            serverSocket.close();
            System.exit(-1);
        }catch(IOException ex){
            System.out.println("Can not stop Server correctly");
            ex.printStackTrace();
        }
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
