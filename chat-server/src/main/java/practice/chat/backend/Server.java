package practice.chat.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.controller.ServerController;
import practice.chat.utils.IOUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by misha on 04.05.16.
 */
public class Server extends Thread {

    private AtomicInteger connectionCounter = new AtomicInteger(0);
    private final int port;
    private Chat chat;
    private ServerSocket serverSocket;
    private ServerController serverController;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public Server(int port, ServerController controller) {
        this.port = port;
        serverController = controller;
    }

    public void initServerSocket() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        chat = Chat.initInstance(this);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectionCounter.incrementAndGet();
                serverController.displayMessage("Socket connected: " + clientSocket);
                chat.addUserToChat(clientSocket);
                serverController.updateConnectionsAmountLabel(connectionCounter.get());
            }
        } catch (IOException ex) {
            LOG.error("Error stack:\n" + ex);
            serverController.onServerFailure();
        } finally {
            chat.stop();
        }
    }

    public void shutdown() {
        serverController.displayMessage("Stopping server...");
        IOUtils.closeQuietly(serverSocket);
    }

    public void emergencyShutdown() { //TODO test
        chat.stop();
    }

    void decreaseConnectionsCounter(String disconnectedClient) {
        serverController.updateConnectionsAmountLabel(connectionCounter.decrementAndGet());
        serverController.displayMessage("Socket closed for: " + disconnectedClient);
    }

}
