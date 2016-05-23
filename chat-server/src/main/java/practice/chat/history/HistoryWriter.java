package practice.chat.history;

import practice.chat.protocol.shared.message.Message;
import practice.chat.utils.ResourceCloser;

import java.io.*;
import java.util.*;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryWriter {

    private final String HOME_DIR = System.getProperty("user.home");
    private final String HISTORY_DIR_NAME = "ChatHistory";
    private final String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;

    public static HistoryWriter getInstance() {
        return new HistoryWriter();
    }

    public void writeHistory(List<Message> history, String room) { //TODO may be it would be better to implement using NIO?
        File dir = new File(HISTORY_PATH);
        File roomFile = new File(HISTORY_PATH + File.separator + room);
        BufferedWriter output = null;
        try {
            dir.mkdir(); // TODO check result
            roomFile.createNewFile(); // TODO check result

            output = new BufferedWriter(new FileWriter(roomFile, true));
            for (Message message : history) {
                output.write(message + "\n");
            }
        } catch (Exception ex) { //TODO add logger
            ex.printStackTrace();
        } finally {
            ResourceCloser.closeQuietly(output);
        }
    }
}

