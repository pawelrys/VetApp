package jwzp.wp.VetApp.service.ErrorMessages;

import java.util.Objects;

public class ResponseErrorMessage {

    public final ErrorType error;
    public final String message;

    public ResponseErrorMessage(ErrorType error) {
        this.error = error;
        this.message = null;
    }

    public ResponseErrorMessage(ErrorType error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorType getType() {
        return error;
    }

    public String getMessage() {
        if (message == null) {
            return error.getDefaultMessage();
        }
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseErrorMessage that = (ResponseErrorMessage) o;
        return error == that.error && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, message);
    }
}
