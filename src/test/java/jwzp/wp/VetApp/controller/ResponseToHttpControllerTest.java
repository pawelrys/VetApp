package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class ResponseToHttpControllerTest {

    @Test
    public void testGetSucceedHttpResponse() {
        ClientRecord clientRecord = new ClientRecord(0, "Jan", "Kowalski");
        var response = Response.succeedResponse(clientRecord);
        var expected = ResponseEntity.ok(clientRecord);

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetClientNotFoundHttpResponse() {
        var response = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.CLIENT_NOT_FOUND));
        var expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorType.CLIENT_NOT_FOUND.getDefaultMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetWrongArgumentsHttpResponse() {
        var response = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        var expected = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS).getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetVisitTimeUnavailableHttpResponse() {
        var response = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var expected = ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE).getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetVisitTimeUnaHttpResponse() {
        var response = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND));
        var expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND).getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetDifferentHttpResponse() {
        var response = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        var expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET).getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }
}
