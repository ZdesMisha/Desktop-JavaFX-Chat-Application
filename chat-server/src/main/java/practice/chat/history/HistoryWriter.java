package practice.chat.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.utils.IOUtils;

import java.io.*;
import java.util.*;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryWriter {

    private final static String HOME_DIR = System.getProperty("user.home");
    private final static String HISTORY_DIR_NAME = "ChatHistory";
    private final static String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;

    private final static Logger LOG = LoggerFactory.getLogger(HistoryWriter.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeHistory(Collection<Message> history, String fileName) {
        File dir = new File(HISTORY_PATH);
        File roomFile = new File(HISTORY_PATH + File.separator + fileName+".txt");
        BufferedWriter output = null;
        try {
            dir.mkdir();
            roomFile.createNewFile();
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(roomFile, true),"UTF-8"));
            for (Message message : history) {
                output.write(message + "\n");
            }
        } catch (IOException ex) {
            LOG.error("Unable to write history to file");
            LOG.error("Error stack:\n" + ex);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}

