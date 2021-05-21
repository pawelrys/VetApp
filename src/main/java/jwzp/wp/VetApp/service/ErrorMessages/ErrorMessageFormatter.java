package jwzp.wp.VetApp.service.ErrorMessages;

public class ErrorMessageFormatter {

    static public String missingField(String name) {
        return "Missing field: " + name;
    }

    static public String missingProperty(String name) {
        return "Missing property: " + name;
    }
}
