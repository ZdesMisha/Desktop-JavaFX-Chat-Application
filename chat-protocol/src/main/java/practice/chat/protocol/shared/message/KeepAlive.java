package practice.chat.protocol.shared.message;

/**
 * Created by misha on 05.05.16.
 */
public class KeepAlive extends MessageTemplate {
    public KeepAlive() {
        super();
        message = "KeepAlive";
    }
}
