package practice.chat.history;

import practice.chat.protocol.shared.message.MessageImplementation;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by misha on 18.05.16.
 */
public class HistoryManager {

    private final String HOME_DIR = System.getProperty("user.home");
    private final String HISTORY_DIR_NAME = "ChatHistory";
    private final String HISTORY_PATH = HOME_DIR + File.separator + HISTORY_DIR_NAME;

    public static HistoryManager getInstance(){
        return new HistoryManager();
    }

    public void writeHistoryToFile(ArrayList<MessageImplementation> history, String room) {
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
            output = new BufferedWriter(new FileWriter(roomFile,true));
            for (MessageImplementation message : history) {
                output.write(message.getMessage() + "\n");
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
        //TODO need to update room list in Server UI
    }

    public ArrayList<String> getRoomHistory(String room) {
        File file = new File(HISTORY_PATH + File.separator + room);
        ArrayList<String> history = new ArrayList<>();
        BufferedReader input = null;
        try {
            if (file.exists()) {
                input = new BufferedReader(new FileReader(file));
                String line;
                while ((line = input.readLine()) != null) {
                    history.add(line);
                }
            }
            return history;
        } catch (IOException ex) {
            ex.printStackTrace();
            return history;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<String> getHistoryFileNames() {
        ArrayList<String> list;
        File dir = new File(HISTORY_PATH);
        if (!dir.exists()) {
            list = new ArrayList<>();
        }
        list = new ArrayList<>(Arrays.asList(dir.list()));
        return list;
    }
}

