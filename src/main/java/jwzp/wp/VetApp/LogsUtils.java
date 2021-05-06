package jwzp.wp.VetApp;

import org.slf4j.Logger;

public class LogsUtils {

    static public String logSaved(Object saved, int id) {
        return "Saved: " + saved.getClass() + " id: " + id;
    }

}
