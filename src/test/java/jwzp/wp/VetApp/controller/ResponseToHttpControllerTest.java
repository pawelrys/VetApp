package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.ResponseToHttp;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        var expected = ResponseEntity.ok(response.get());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetClientNotFoundHttpResponse() {
        var response = Response.errorResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
        var expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseErrorMessage.CLIENT_NOT_FOUND.getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetWrongArgumentsHttpResponse() {
        var response = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        var expected = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseErrorMessage.WRONG_ARGUMENTS.getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetVisitTimeUnavailableHttpResponse() {
        var response = Response.errorResponse(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
        var expected = ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE.getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }

    @Test
    public void testGetVisitTimeUnaHttpResponse() {
        var response = Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
        var expected = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseErrorMessage.VISIT_NOT_FOUND.getMessage());

        var result = ResponseToHttp.getDefaultHttpResponse(response);

        assert Objects.equals(result, expected);
    }
}
