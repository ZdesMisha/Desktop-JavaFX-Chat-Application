package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.Optional;

import static practice.chat.history.HistoryManager.readHistory;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryController {

    @FXML
    private Label fileName;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private TextArea textArea;

    private int page = 1;

    @FXML
    public void initialize() {
        previousButton.setDisable(true);
    }

    public void handlePreviousButton() {
        nextButton.setDisable(false);
        page--;
        showHistory();
        if (page <= 1) {
            previousButton.setDisable(true);
        }
    }

    public void handleNextButton() {
        previousButton.setDisable(false);
        page++;
        showHistory();
    }

    public void showHistory() {
        textArea.clear();
        Optional<List<String>> history = readHistory(fileName.getText(), page);
        if (history.isPresent()) {
            if (history.get().isEmpty()) {
                nextButton.setDisable(true);
                textArea.appendText("End of file");
            } else {
                for (String line : history.get()) {
                    textArea.appendText(line + "\n");
                }
            }
        } else {
            showAlertBox("File does not exist");
        }
    }

    private void showAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    public void setRoomLabel(String name) {
        fileName.setText(name);
    }

}
