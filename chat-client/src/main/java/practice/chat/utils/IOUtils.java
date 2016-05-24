package practice.chat.utils;

/**
 * Created by misha on 23.05.16.
 */
public class IOUtils {

    public static void closeQuietly(AutoCloseable closeable){
        try{
            closeable.close();
        }catch (Exception ex){ //TODO logger
            ex.printStackTrace();
        }
    }
}
