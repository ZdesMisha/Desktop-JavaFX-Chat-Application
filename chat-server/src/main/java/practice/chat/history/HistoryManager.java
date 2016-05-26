package practice.chat.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by misha on 21.05.16.
 */
public class HistoryManager {

    private final static String HOME_DIR = System.getProperty("user.home");
    private final static String HISTORY_DIR_NAME = "ChatHistory";
    private final static String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;

    private final static Logger LOG = LoggerFactory.getLogger(HistoryManager.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized static void writeHistory(Collection<Message> history, String fileName) {
        File dir = new File(HISTORY_PATH);
        File roomFile = new File(HISTORY_PATH + File.separator + fileName + ".txt");
        BufferedWriter output = null;
        try {
            dir.mkdir();
            roomFile.createNewFile();
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(roomFile, true), "UTF-8"));
            for (Message message : history) {
                output.write(message + "\n");
            }
        } catch (IOException ex) {
            LOG.error("Unable to write history to file", ex);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    public static Optional<List<String>> readHistory(String room, int page) {
        int recordsPerPage = 25;
        long startRecord = recordsPerPage * (page - 1);
        int recordsCounter = 0;
        ArrayList<String> history = new ArrayList<>();
        Path path = Paths.get(HISTORY_PATH + File.separator + room);
        try {
            Charset charset = Charset.forName("UTF-8");
            if (Files.exists(path)) {

                Iterator<String> iterator = Files.lines(Paths.get(HISTORY_PATH + File.separator + room), charset)
                        .skip(startRecord)
                        .iterator();

                while (iterator.hasNext() && recordsCounter < recordsPerPage) {
                    recordsCounter++;
                    history.add(iterator.next());
                }
            } else {
                return Optional.empty();
            }
            return Optional.of(history);
        } catch (IOException ex) {
            LOG.error("Unable to read history", ex);
            return Optional.empty();
        }
    }

    public static List<String> getHistoryFileNames() {
        ArrayList<String> list = new ArrayList<>();
        File dir = new File(HISTORY_PATH);
        if (dir.exists() && dir.isDirectory()) {
            for (String file : dir.list()) {
                if (file.matches(".*Room.*\\.txt$")) {
                    list.add(file);
                }
            }
        }
        return list;
    }
}
