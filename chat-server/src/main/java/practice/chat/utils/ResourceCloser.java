package practice.chat.utils;

/**
 * Created by misha on 23.05.16.
 */
public class ResourceCloser {

    public static void closeQuietly(AutoCloseable closeable){
        try{
            if (closeable!=null) {
                closeable.close();
            }
        }catch (Exception ex){ //TODO logger
            ex.printStackTrace();
        }
    }
}
