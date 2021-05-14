package jwzp.wp.VetApp.service.ErrorMessages;

public enum ErrorType {
    SOMETHING_WENT_WRONG("Something went wrong"),

    VISIT_NOT_FOUND("Visit not found"),

    VISIT_TIME_UNAVAILABLE("It's too late to make an appointment"),

    CLIENT_NOT_FOUND("Client not found"),

    PET_NOT_FOUND("Pet not found"),

    VET_NOT_FOUND("Vet not found"),

    OFFICE_NOT_FOUND("Office not found"),

    BUSY_VET("Vet is not available at this time"),

    BUSY_OFFICE("Office is not available at this time"),

    WRONG_ARGUMENTS("Wrong arguments provided");

    private final String defaultMessage;

    ErrorType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
