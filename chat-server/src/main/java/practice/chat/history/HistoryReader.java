package practice.chat.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by misha on 21.05.16.
 */
public class HistoryReader {

    private static final String HOME_DIR = System.getProperty("user.home");
    private static final String HISTORY_DIR_NAME = "ChatHistory";
    private static final String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;
    private static final Logger LOG = LoggerFactory.getLogger(HistoryReader.class);


    public static HistoryReader getInstance() {
        return new HistoryReader();
    }


    public static Optional<List<String>> readHistory(String room, int page) { //TODO optional ???
        final int recordsPerPage = 20;
        long startRecord = recordsPerPage * (page - 1) + 1;
        int recordsCounter = 0;
        ArrayList<String> history = new ArrayList<>();
        Path path = Paths.get(HISTORY_PATH + File.separator + room);
        try {
            Charset charset = Charset.forName("UTF-8");
            if (Files.exists(path)) {
                Iterator<String> iterator = Files.lines(Paths.get(HISTORY_PATH + File.separator + room), charset).skip(startRecord - 1).iterator();
                while (iterator.hasNext() && recordsCounter < recordsPerPage) {
                    recordsCounter++;
                    history.add(iterator.next());
                }
            } else {
                return Optional.empty();
            }
            return Optional.of(history);
        } catch (IOException ex) {
            System.out.println("ERROR!");
            LOG.error("Unable to read history");
            LOG.error("Error stack:\n" + ex);
            return Optional.empty();
        }
    }

    public static List<String> getHistoryFileNames() { //TODO ??
        ArrayList<String> list = new ArrayList<>();
        File dir = new File(HISTORY_PATH);
        if (dir.exists() && dir.isDirectory()) {
            if (dir.list().length != 0) {
                for (String file : dir.list()) {
                    if (file.matches(".*Room.*\\.txt$")) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }
}
