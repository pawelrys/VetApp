package jwzp.wp.VetApp;

import org.slf4j.Logger;

public class LogsUtils {

    static public void logSaved(Logger logger, Object saved, int id) {
        logger.info("Saved: " + saved.getClass() + " id: " + id);
    }

}
