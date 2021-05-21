package jwzp.wp.VetApp.service.ErrorMessages;

import java.util.LinkedList;
import java.util.List;

public class ErrorMessagesBuilder {

    private final List<String> subMessages = new LinkedList<>();

    public void addToMessage(String msg){
        subMessages.add(msg);
    }

    public boolean isEmpty(){
        return subMessages.isEmpty();
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
