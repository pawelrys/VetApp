package jwzp.wp.VetApp.controller.api.utils;

import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import jwzp.wp.VetApp.service.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseToHttp {

    public static HttpStatus getCode(ResponseErrorMessage errorMessage) {
        switch (errorMessage) {
            case VISIT_NOT_FOUND:
            case CLIENT_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case WRONG_ARGUMENTS:
                return HttpStatus.NOT_ACCEPTABLE;
            case VISIT_TIME_UNAVAILABLE:
                return HttpStatus.CONFLICT;
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    public static ResponseEntity<?> getDefaultHttpResponse(Response<?> response) {
        return response.succeed()
                ? ResponseEntity.ok(response.get())
                : getFailureResponse(response.getError());
    }

    public static ResponseEntity<?> getFailureResponse(ResponseErrorMessage err) {
        return ResponseEntity.status(getCode(err)).body(err.getMessage());
    }
}
