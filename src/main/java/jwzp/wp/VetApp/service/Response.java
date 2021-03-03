package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitRecord;
import org.springframework.http.ResponseEntity;

public class Response<T> {

    private final T resource;
    private final ResponseErrorMessage errorMessage;

    private Response(T resource){
        this.resource = resource;
        this.errorMessage = null;
    }

    private Response(ResponseErrorMessage errorMessage){
        this.resource = null;
        this.errorMessage = errorMessage;
    }

    public static <T> Response<T> succeedResponse(T resource){
        return new Response<T>(resource);
    }

    public static <T> Response<T> errorResponse(ResponseErrorMessage message){
        return new Response<T>(message);
    }

    public ResponseEntity<?> get(){
        return resource != null
                ? ResponseEntity.ok(resource)
                : ResponseEntity.badRequest().body(errorMessage.getMessage());
    }

}

