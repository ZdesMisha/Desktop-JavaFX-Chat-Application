package practice.chat.history;

import practice.chat.protocol.shared.message.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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


    public ArrayList<String> readHistory(String room, int page) {
        long startRecord = RECORDS_PER_PAGE * (page - 1) + 1;
        int readsCounter = 0;
        ArrayList<String> history = new ArrayList<>();
        Path path = Paths.get(HISTORY_PATH + File.separator + room);
        try {
            if(Files.exists(path)) {
                Iterator<String> iterator = Files.lines(Paths.get(HISTORY_PATH + File.separator + room)).skip(startRecord - 1).iterator();
                while (iterator.hasNext() && readsCounter < RECORDS_PER_PAGE) {
                    readsCounter++;
                    history.add(iterator.next());
                }
            } else {
                return null;
            }
            return history;
        } catch (IOException ex) {//TODO add logger
            ex.printStackTrace();
            return null;
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
