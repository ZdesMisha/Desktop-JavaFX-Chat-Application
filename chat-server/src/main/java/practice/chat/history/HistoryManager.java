package practice.chat.history;

import practice.chat.protocol.shared.message.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryManager { //TODO does it should provide new instance for each request or singleton will be enough?

    private final String HOME_DIR = System.getProperty("user.home");
    private final String HISTORY_DIR_NAME = "ChatHistory";
    private final String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;
    private final int RECORDS_PER_PAGE = 20;

    public static HistoryManager getInstance() {
        return new HistoryManager();
    }

    public void writeHistory(ArrayList<Message> history, String room) { //TODO may be it would be better to implement using NIO?
        File dir = new File(HISTORY_PATH);
        File roomFile = new File(HISTORY_PATH + File.separator + room);
        BufferedWriter output = null;
        try {
            if (!dir.exists()) {
                dir.createNewFile();
            }
            if (!roomFile.exists()) {
                roomFile.createNewFile();
            }
            output = new BufferedWriter(new FileWriter(roomFile, true));
            for (Message message : history) {
                output.write(message + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<String> readHistory(String room, int page) {
        File file = new File(HISTORY_PATH + File.separator + room);
        long startRecord = RECORDS_PER_PAGE * (page - 1) + 1;
        int readsCounter = 0;
        ArrayList<String> history = new ArrayList<>();
        try {
            if (file.exists()) {
                Iterator<String> iterator = Files.lines(Paths.get(HISTORY_PATH + File.separator + room)).skip(startRecord - 1).iterator();
                while (iterator.hasNext() && readsCounter < RECORDS_PER_PAGE) {
                    readsCounter++;
                    history.add(iterator.next());
                }
            } else {
                history.add("File not found");
            }
            return history;
        } catch (IOException ex) {
            ex.printStackTrace();
            return history;
        }
    }

    public ArrayList<String> getHistoryFileNames() { //TODO file validation
        ArrayList<String> list;
        File dir = new File(HISTORY_PATH);
        if (!dir.exists()) {
            list = new ArrayList<>();
        }
        list = new ArrayList<>(Arrays.asList(dir.list()));
        return list;
    }
}

