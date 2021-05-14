package jwzp.wp.VetApp.service.ErrorMessages;

import java.util.List;

public class ErrorMessagesBuilder {

    private List<String> subMessages;

    public void addToMessage(String msg){
        subMessages.add(msg);
    }

    public ResponseErrorMessage build() {
        return build("");
    }

    public ResponseErrorMessage build(String prefix) {
        return new ResponseErrorMessage(
                ErrorType.SOMETHING_WENT_WRONG,
                prefix + String.join(", ", subMessages)
        );
    }

    public static ResponseErrorMessage simpleError(ErrorType error, String message) {
        return new ResponseErrorMessage(error, message);
    }

    public static ResponseErrorMessage simpleError(ErrorType error) {
        return new ResponseErrorMessage(error);
    }
}
