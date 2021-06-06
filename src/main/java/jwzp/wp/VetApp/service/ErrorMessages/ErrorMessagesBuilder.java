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
        return build(ErrorType.SOMETHING_WENT_WRONG);
    }

    public ResponseErrorMessage build(ErrorType error) {
        return new ResponseErrorMessage(
                error,
                String.join("\n", subMessages)
        );
    }

    public static ResponseErrorMessage simpleError(ErrorType error, String message) {
        return new ResponseErrorMessage(error, message);
    }

    public static ResponseErrorMessage simpleError(ErrorType error) {
        return new ResponseErrorMessage(error);
    }
}
