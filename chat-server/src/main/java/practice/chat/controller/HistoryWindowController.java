package practice.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import practice.chat.history.HistoryManager;
import practice.chat.main.SceneDispatcher;

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

    private SceneDispatcher sceneDispatcher;

    private HistoryManager historyManager = HistoryManager.getInstance();

    public void setSceneDispatcher(SceneDispatcher sceneDispatcher) {
        this.sceneDispatcher = sceneDispatcher;
    }

    @FXML
    public void initialize() {
        roomName.setText("MainRoom"); //TODO check if room name is empty( add on ServerController side)
        previousButton.setDisable(true);
        displayHistory();
    }

    public void handlePreviousButton() {
        if (page <= 1) {
            previousButton.setDisable(true);
        } else {
            page--;
        }
        displayHistory();
    }

    public void handleNextButton() {
        previousButton.setDisable(false);
        page++;
        displayHistory();
    }

    public void displayHistory() {
        textArea.clear();
        for (String line : historyManager.getRoomHistory(roomName.getText(), page)) {
            textArea.appendText(line + "\n");
        }
    }

}
