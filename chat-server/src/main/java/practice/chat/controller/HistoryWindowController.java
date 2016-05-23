package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import practice.chat.dispatcher.ApplicationDispatcher;
import practice.chat.history.HistoryReader;

import java.util.List;
import java.util.Optional;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryWindowController {

    @FXML
    public Label roomName;

    @FXML
    public Button nextButton;

    @FXML
    public Button previousButton;

    @FXML
    private TextArea textArea;

    private int page = 1;

    private ApplicationDispatcher applicationDispatcher;

    private HistoryReader historyReader = HistoryReader.getInstance();

    public void setApplicationDispatcher(ApplicationDispatcher applicationDispatcher) {
        this.applicationDispatcher = applicationDispatcher;
    }

    @FXML
    public void initialize() {
        previousButton.setDisable(true);
    }

    public void handlePreviousButton() {
        nextButton.setDisable(false);
        if (page <= 1) {
            previousButton.setDisable(true);
        } else {
            page--;
        }
        showHistory();
    }

    public void handleNextButton() {
        previousButton.setDisable(false);
        page++;
        showHistory();
    }

    public void showHistory() {
        textArea.clear();
        Optional<List<String>> history = Optional.of(historyReader.readHistory(roomName.getText(), page)); //TODO to read about optional
        if (history.isPresent()) {
            if (history.get().isEmpty()) {
                nextButton.setDisable(true);
            } else {
                for (String line : history.get()) {
                    textArea.appendText(line + "\n");
                }
            }
        } else {
            showAlertBox("File is not exist");
        }
    }
    private void showAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public void setRoomLable(String name){
        roomName.setText(name);
    }

}
