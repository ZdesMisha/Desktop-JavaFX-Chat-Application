package practice.chat.protocol.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by misha on 23.05.16.
 */
public final class IOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    private IOUtils() {
        // utility
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            LOG.error("Unable close resources properly", ex);
        }
    }
}
