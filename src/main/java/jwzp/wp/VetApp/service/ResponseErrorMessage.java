package jwzp.wp.VetApp.service;

public enum ResponseErrorMessage {
    VISIT_NOT_FOUND("Visit not found"),
    VISIT_TIME_UNAVAILABLE("Time of visit is unavailable"),

    CLIENT_NOT_FOUND("Client not found"),

    PET_NOT_FOUND("Pet not found"),

    VET_NOT_FOUND("Vet not found"),

    WRONG_ARGUMENTS("Wrong arguments provided");

    private final String message;

    ResponseErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
