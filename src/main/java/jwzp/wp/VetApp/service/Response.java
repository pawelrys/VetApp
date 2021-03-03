package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitRecord;
import org.springframework.http.ResponseEntity;

public class Response {

    private final VisitRecord visit;
    private final ResponseErrorMessage errorMessage;

    public Response(VisitRecord visit){
        this.visit = visit;
        this.errorMessage = null;
    }

    public Response(ResponseErrorMessage errorMessage){
        this.visit = null;
        this.errorMessage = errorMessage;
    }

    public ResponseEntity<?> get(){
        return visit != null
                ? ResponseEntity.ok(visit)
                : ResponseEntity.badRequest().body(errorMessage.getMessage());
    }

}

