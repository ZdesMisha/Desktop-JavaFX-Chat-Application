package practice.chat.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import practice.chat.protocol.shared.messages.Message;
import practice.chat.protocol.shared.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Created by misha on 21.05.16.
 */
public class HistoryManager {

    private final static Charset CHARSET = Charset.forName("UTF-8");
    private final static String HOME_DIR = System.getProperty("user.home");
    private final static String HISTORY_DIR_NAME = "ChatHistory";
    private final static String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;
    private final static int PAGE_SIZE = 50;

    private final static Logger LOG = LoggerFactory.getLogger(HistoryManager.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized static void writeHistory(Collection<Message> history, String fileName) {
        Path path = Paths.get(HISTORY_PATH + File.separator + fileName + ".txt");
        try {
            Files.write(path,
                    history.stream().map(Object::toString).collect(Collectors.toList()),
                    CHARSET, CREATE, APPEND);
        } catch (IOException ex) {
            LOG.error("Unable to write history to file", ex);
        }
    }

    public static Optional<List<String>> readHistory(String room, int page) {

        Path path = Paths.get(HISTORY_PATH + File.separator + room);

        if (Files.exists(path)) {
            try (Stream<String> lines = Files.lines(path, CHARSET)) {

                long startRecord = PAGE_SIZE * (page - 1);
                Iterator<String> iterator = lines
                        .skip(startRecord)
                        .iterator();

                ArrayList<String> history = new ArrayList<>();
                int recordsCounter = 0;
                while (iterator.hasNext() && recordsCounter < PAGE_SIZE) {
                    recordsCounter++;
                    history.add(iterator.next());
                }
                return Optional.of(history);
            } catch (IOException ex) {
                LOG.error("Unable to read history", ex);
            }
        }

        return Optional.empty();
    }

    public static List<String> getHistoryFileNames() {

        List<String> list = new ArrayList<>();
        Path path = Paths.get(HISTORY_PATH);

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {

            paths.forEach(item -> {
                String fileName = item.getFileName().toString();
                if (fileName.matches(".*Room.*\\.txt$") && Files.isRegularFile(item)) {
                    list.add(fileName);
                }
            });

        } catch (IOException ex) {
            LOG.error("Unable to list history files", ex);
        }
        return list;
    }
}
