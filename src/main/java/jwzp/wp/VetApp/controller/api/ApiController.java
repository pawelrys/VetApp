package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.service.VisitsService;
import org.apache.coyote.Response;
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
                ? ResponseEntity.ok(visit)
                : ResponseEntity.badRequest().body("not found visit with id: " + id);
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable int id, @RequestBody VisitData newData) {
        Optional<VisitRecord> updated = service.updateVisit(id, newData);
        return updated.isPresent()
                ? ResponseEntity.ok(updated)
                : ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<?> addVisit(@RequestBody VisitData visit) {
        Optional<VisitRecord> result = service.addVisit(visit);
        return result.isPresent()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable int id) {
        Optional<VisitRecord> result = service.delete(id);
        return result.isPresent()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().build();
    }
}
