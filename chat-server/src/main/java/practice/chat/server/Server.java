package practice.chat.server;

import practice.chat.controller.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by misha on 04.05.16.
 */
public class Server extends Thread {

    private static final int PORT = 1234; //TODO move to config
    private boolean running = false;
    private Chat chat;
    private ServerSocket serverSocket;
    public ServerController serverController;

    public Server(ServerController controller) {
        serverController = controller;
    }

    public void run() {
        running = true;
        try {
            serverSocket = new ServerSocket(PORT);
            chat = Chat.getInstance();
            serverController.displayMessage("Server started successfully");
            while (running) {
                serverController.displayMessage("Waiting for a connection...");
                Socket clientSocket = serverSocket.accept();
                serverController.displayMessage("New client connected, socket: " + clientSocket);
                chat.addUserToChat(clientSocket);
            }
        } catch (Exception ex) {
            System.out.println("Server failure"); //TODO  logger
            ex.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            running = false;
            chat.stop();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Can not shutdown Server correctly");
            ex.printStackTrace();
        }
    }

}
