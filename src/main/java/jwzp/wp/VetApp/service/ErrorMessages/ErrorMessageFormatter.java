package jwzp.wp.VetApp.service.ErrorMessages;

public class ErrorMessageFormatter {

    static public String missingField(String name) {
        return "Missing field: " + name;
    }

    static public String missingProperty(String name) {
        return "Missing property: " + name;
    }

    static public String doesNotExists(Class type, int id) {
        return "Object of class " + type.getName() + " with id of " + id + " does not exists";
    }
}
