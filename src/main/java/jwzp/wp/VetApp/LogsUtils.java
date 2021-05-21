package jwzp.wp.VetApp;

import java.lang.reflect.Field;

public class LogsUtils {

    static public String logSaved(Object saved, int id) {
        return "Saved: " + saved.getClass() + " id: " + id;
    }

    static public String logDeleted(Object deleted, int id) {
        return "Deleted: " + deleted.getClass() + " id: " + id;
    }

    static public String logUpdated(Object updated, int id) {
        return "Updated: " + updated.getClass() + " id: " + id;
    }

    static public String logMissingData(Object obj, Field field) {
        return "Missing data \"" + field.getName() + "\" in " + obj.toString();
    }

    static public String logTimeUnavailability() {
        return "Request data don't match with constraints";
    }

    static public String logNotFoundObject(Class type, int id) {
        return "Object: " + type + " with id: " + id + " not found";
    }

    static public String logException(Exception e) {
        return "Exception occurred: " + e.getClass() + " message: " + e.getMessage();
    }
}
