package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import jwzp.wp.VetApp.service.VisitsService;
import jwzp.wp.VetApp.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping(path="/api/visits")
@RestController
public class ApiController {

    private final VisitsService service;

    @Autowired
    private ApiController(VisitsService service) {
        this.service = service;
    }

    @GetMapping(path="/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping
    public ResponseEntity<?> getAllVisits() {
        return ResponseEntity.ok().body(service.getAllVisits());
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVisit(@PathVariable int id) {
        Optional<VisitRecord> visit = service.getVisit(id);
        return visit.isPresent()
                ? ResponseEntity.ok(visit.get())
                : ResponseEntity.badRequest().body(ResponseErrorMessage.VISIT_NOT_FOUND.getMessage());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable int id, @RequestBody VisitData newData) {
        Response<?> updated = service.updateVisit(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(updated.get())
                : ResponseEntity.badRequest().body(updated.get());
    }

    @PostMapping
    public ResponseEntity<?> addVisit(@RequestBody VisitData visit) {
        Response<?> result = service.addVisit(visit);
        return result.succeed()
                ? ResponseEntity.ok(result.get())
                : ResponseEntity.badRequest().body(result.get());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable int id) {
        Response<?> result = service.delete(id);
        return result.succeed()
                ? ResponseEntity.ok(result.get())
                : ResponseEntity.badRequest().body(result.get());
    }
}
