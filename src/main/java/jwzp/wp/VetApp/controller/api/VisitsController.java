package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping(path="/api/visits")
@RestController
@Component
public class VisitsController {

    private final VisitsService visitsService;

    @Autowired
    private VisitsController(VisitsService visitsService) {
        this.visitsService = visitsService;

    }

    @GetMapping(path="/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping
    public ResponseEntity<?> getAllVisits() {
        return ResponseEntity.ok().body(visitsService.getAllVisits());
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVisit(@PathVariable int id) {
        Optional<VisitRecord> visit = visitsService.getVisit(id);
        return visit.isPresent()
                ? ResponseEntity.ok(visit.get())
                : ResponseToHttp.getFailureResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable int id, @RequestBody VisitData newData) {
        Response<?> updated = visitsService.updateVisit(id, newData);
        return ResponseToHttp.getDefaultHttpResponse(updated);
    }

    @PostMapping
    public ResponseEntity<?> addVisit(@RequestBody VisitData visit) {
        Response<?> result = visitsService.addVisit(visit);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable int id) {
        Response<?> result = visitsService.delete(id);
        return ResponseToHttp.getDefaultHttpResponse(result);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void automaticallyClosePastVisits() {
        var result = visitsService.updatePastVisitsStatusTo(Status.CLOSED_AUTOMATICALLY);
        ResponseEntity.ok().body(result);
    }
}
