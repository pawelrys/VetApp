package jwzp.wp.VetApp;

public class LogsUtils {

    static public String logSaved(Object saved, int id) {
        return "Saved: " + saved.getClass() + " id: " + id;
    }

    static public String logDeleted(Object saved, int id) {
        return "Deleted: " + saved.getClass() + " id: " + id;
    }

    static public String logUpdated(Object saved, int id) {
        return "Updated: " + saved.getClass() + " id: " + id;
    }

    static public String logWrongData(Object saved) {
        return "Wrong data: " + saved.getClass();
    }
}
