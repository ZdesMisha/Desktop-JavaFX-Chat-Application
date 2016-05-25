package practice.chat.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.dispatcher.ApplicationDispatcher;
import practice.chat.backend.Server;

import java.util.List;

import static practice.chat.history.HistoryReader.getHistoryFileNames;

/**
 * Created by misha on 17.05.16.
 */
public class ServerController {

    @FXML
    private TextArea serverLog;
    @FXML
    private Button runButton;
    @FXML
    private Button shutdownButton;
    @FXML
    public Label connectionsAmount;
    @FXML
    private TextField portField;

    public ChoiceBox<String> roomChoice;

    private ApplicationDispatcher applicationDispatcher;

    private static final String PORT_FORMAT_PATTERN =
            "^(\\d{0,5})?$";
    private static final String VALID_PATTERN = "\\d+";

    private TextFormatter<String> portFieldFormatter = new TextFormatter<>(c -> c
            .getControlNewText().matches(PORT_FORMAT_PATTERN) ? c : null);

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);


    @FXML
    public void initialize() {
        portField.setTextFormatter(portFieldFormatter);
        connectionsAmount.setText("0");
        updateRoomList(getHistoryFileNames());
    }


    public void handleRunButton() {
        String port = portField.getText();

        if (!isValidPort(port)) {
            showAlertBox("Port value must be more that 1024 \n" +
                    "and less than 65536");
        } else {
            displayMessage("Starting server...");
            applicationDispatcher.server = new Server(Integer.parseInt(port), applicationDispatcher.getServerController());
            try {
                applicationDispatcher.server.initServerSocket();
                applicationDispatcher.server.start();
                onServerStart();
            } catch (Exception ex) {
                LOG.error("Unable to initialize server socket");
                LOG.error("Error stack: \n" + ex);
                showAlertBox("Unable to start server");
                onServerFailure();
            }
        }
    }

    public void handleUpdateHistoryListButton() {
        updateRoomList(getHistoryFileNames());
    }

    public void handleShutdownButton() {
        applicationDispatcher.server.shutdown();
    }

    public void handleShowHistoryButton() {
        if (roomChoice.getValue() == null) {
            showAlertBox("You have to chose room!");
        } else {
            applicationDispatcher.openHistoryWindow(roomChoice.getValue());
        }
    }

    public void displayMessage(String message) {
        serverLog.appendText(message + "\n");
        LOG.info(message + "\n");
    }

    private void updateRoomList(List<String> rooms) {
        roomChoice.getItems().clear();
        for (String room : rooms) {
            roomChoice.getItems().add(room);
        }
    }

    public void updateConnectionsAmountLabel(int amount) {
        Platform.runLater(() -> connectionsAmount.setText("" + amount));
    }

    private void onServerStart() {
        Platform.runLater(() -> {
            runButton.setDisable(true);
            shutdownButton.setDisable(false);
            portField.setDisable(true);
            displayMessage("SERVER IS UP!");
        });
    }

    public void onServerFailure() {
        Platform.runLater(() -> {
            runButton.setDisable(false);
            shutdownButton.setDisable(true);
            portField.setDisable(false);
            displayMessage("SERVER IS DOWN");
        });


    }

    private boolean isValidPort(String port) {
        if (!port.matches(VALID_PATTERN)) {
            return false;
        }
        int parsedPort = Integer.parseInt(port);
        return parsedPort > 1024 && parsedPort < 65536;
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
