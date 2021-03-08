package jwzp.wp.VetApp.service;

public enum ResponseErrorMessage {
    VISIT_NOT_FOUND("Visit not found"),
    VISIT_TIME_UNAVAILABLE("Time of visit is unavailable"),
    WRONG_ARGUMENTS("Wrong arguments provided");

    private final String message;

    ResponseErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
