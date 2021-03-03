package jwzp.wp.VetApp.service;

import org.springframework.http.ResponseEntity;

public class Response<T> {

    private final T resource;
    private final ResponseErrorMessage errorMessage;

    public Response(T resource){
        this.resource = resource;
        this.errorMessage = null;
    }

    public Response(ResponseErrorMessage errorMessage){
        this.resource = null;
        this.errorMessage = errorMessage;
    }

    public ResponseEntity<?> get(){
        return resource != null
                ? ResponseEntity.ok(resource)
                : ResponseEntity.badRequest().body(errorMessage.getMessage());
    }

}

