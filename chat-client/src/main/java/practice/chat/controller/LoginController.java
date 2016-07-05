package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.backend.Client;
import practice.chat.dispatcher.ApplicationDispatcher;

/**
 * Created by misha on 10.05.16.
 */
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private static final String IP_ADDRESS_FORMAT_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])?\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])?\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])?\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])?$";

    private static final String IP_ADDRESS_VALID_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String PORT_VALID_PATTERN = "\\d+";

    private static final String PORT_FORMAT_PATTERN = "^(\\d{0,5})?$";

    private static final int MAX_LOGIN_SIZE = 10;

    private static final TextFormatter<String> loginTextLengthFormatter = new TextFormatter<>(c -> c
            .getControlNewText().length() > MAX_LOGIN_SIZE ? null : c);
    private static final TextFormatter<String> ipAddressFieldFormatter = new TextFormatter<>(c -> c
            .getControlNewText().matches(IP_ADDRESS_FORMAT_PATTERN) ? c : null);
    private static final TextFormatter<String> portFieldFormatter = new TextFormatter<>(c -> c
            .getControlNewText().matches(PORT_FORMAT_PATTERN) ? c : null);

    @FXML
    private TextField loginField;
    @FXML
    private TextField ipAddressField;
    @FXML
    private TextField portField;

    private ApplicationDispatcher applicationDispatcher;

    @FXML
    public void initialize() {
        loginField.setTextFormatter(loginTextLengthFormatter);
        ipAddressField.setTextFormatter(ipAddressFieldFormatter);
        portField.setTextFormatter(portFieldFormatter);
    }

    public void handleJoinInButton() {
        String ip = ipAddressField.getText();
        String port = portField.getText();
        String login = loginField.getText();

        if (!isValidLogin(login)) {
            showAlertBox("Login must be from 2 to 15 letters or digits");
            return;
        } else if (!isValidIp(ip)) {
            showAlertBox("Ip address is not valid");
            return;
        } else if (!isValidPort(port)) {
            showAlertBox("Port value must be more that 1024 \nand less than 65536");
            return;
        }

        try {
            applicationDispatcher.setLoginLabel(login);
            applicationDispatcher.setClient(new Client(login, ip, Integer.parseInt(port),
                    applicationDispatcher.getChatController()));
            applicationDispatcher.getClient().establishConnection();
            applicationDispatcher.getClient().start();
            applicationDispatcher.switchToChat();
        } catch (Exception ex) {
            applicationDispatcher.switchToLogin();
            showAlertBox("Can not establish connection with server\n" +
                    "Possible reasons:\n" +
                    "* Server is offline\n" +
                    "* Wrong server address");
            LOG.error("Can not establish connection with server", ex);
        }
    }

    private static boolean isValidLogin(String login) {
        return login.matches("[a-zA-Z0-9]{2,20}");
    }

    private static boolean isValidPort(String port) {
        if (!port.matches(PORT_VALID_PATTERN)) {
            return false;
        }
        int parsedPort = Integer.parseInt(port);
        return parsedPort > 1024 && parsedPort < 65536;
    }

    private static boolean isValidIp(String ip) {
        return ip.matches(IP_ADDRESS_VALID_PATTERN);
    }

    private void showAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public void setApplicationDispatcher(ApplicationDispatcher applicationDispatcher) {
        this.applicationDispatcher = applicationDispatcher;
    }

}
