package practice.chat.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.controller.ServerController;
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by misha on 04.05.16.
 */
public class Server extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final AtomicInteger connectionCounter = new AtomicInteger(0);
    private final ServerController serverController;
    private final int port;

    private ServerSocket serverSocket;

    public Server(int port, ServerController controller) {
        this.port = port;
        serverController = controller;
    }

    public void initServerSocket() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        Chat chat = Chat.initInstance(this);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectionCounter.incrementAndGet();
                serverController.displayMessage("Socket connected: " + clientSocket);
                chat.addUserToChat(clientSocket);
                serverController.updateConnectionsAmountLabel(connectionCounter.get());
            }
        } catch (IOException ex) {
            LOG.error("Error stack:" + ex);
            serverController.onServerFailure();
        } finally {
            chat.stop();
            IOUtils.closeQuietly(serverSocket);
        }
    }

    public void shutdown() {
        serverController.displayMessage("Stopping server...");
        IOUtils.closeQuietly(serverSocket);
    }


    void decreaseConnectionsCounter() {
        serverController.updateConnectionsAmountLabel(connectionCounter.decrementAndGet());
        serverController.displayMessage("Socket closed");
    }

}
