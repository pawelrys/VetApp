package jwzp.wp.VetApp;

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

    static public String logMissingData(Object obj) {
        return "Missing data in DTO: " + obj.getClass();
    }
}
