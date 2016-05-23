package practice.chat.history;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by misha on 21.05.16.
 */
public class HistoryReader {

    private final String HOME_DIR = System.getProperty("user.home");
    private final String HISTORY_DIR_NAME = "ChatHistory";
    private final String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;
    private final int RECORDS_PER_PAGE = 20;

    public static HistoryReader getInstance() {
        return new HistoryReader();
    }


    public Optional<List<String>> readHistory(String room, int page) { //TODO ???
        long startRecord = RECORDS_PER_PAGE * (page - 1) + 1;
        int readsCounter = 0;
        ArrayList<String> history = new ArrayList<>();
        Path path = Paths.get(HISTORY_PATH + File.separator + room);
        try {
            if (Files.exists(path)) {
                Iterator<String> iterator = Files.lines(Paths.get(HISTORY_PATH + File.separator + room)).skip(startRecord - 1).iterator();
                while (iterator.hasNext() && readsCounter < RECORDS_PER_PAGE) {
                    readsCounter++;
                    history.add(iterator.next());
                }
            } else {
                return Optional.empty();
            }
            return Optional.of(history);
        } catch (IOException ex) {//TODO add logger
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public List<String> getHistoryFileNames() { //TODO file validation
        ArrayList<String> list;
        File dir = new File(HISTORY_PATH);
        if (!dir.exists()) {
            list = new ArrayList<>();
        }
        list = new ArrayList<>(Arrays.asList(dir.list()));
        return list;
    }
}
