package practice.chat;

import java.net.Socket;

/**
 * Created by misha on 06.05.16.
 */
public class ClientConnection {

    Socket socket;


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }
}
