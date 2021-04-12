package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.utils.TimeIntervalData;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        List<VisitRecord> visits = visitsService.getAllVisits();
        for(var visit : visits){
            addLinksToEntity(visit);
        }
        Link self = linkTo(VisitsController.class).withSelfRel();
        CollectionModel<VisitRecord> result = CollectionModel.of(visits, self);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVisit(@PathVariable int id) {
        Optional<VisitRecord> visit = visitsService.getVisit(id);
        if (visit.isPresent()){
            return ResponseEntity.ok(addLinksToEntity(visit.get()));
        } else {
            return ResponseToHttp.getFailureResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
        }
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable int id, @RequestBody VisitData newData) {
        Response<VisitRecord> updated = visitsService.updateVisit(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(addLinksToEntity(updated.get()))
                : ResponseToHttp.getFailureResponse(updated.getError());
    }

    @PostMapping
    public ResponseEntity<?> addVisit(@RequestBody VisitData visit) {
        Response<VisitRecord> result = visitsService.addVisit(visit);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable int id) {
        Response<VisitRecord> result = visitsService.delete(id);
        return result.succeed()
                ? ResponseEntity.ok(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @GetMapping(path = "available-time-slots")
    public ResponseEntity<?> getAvailableTimeSlots(@RequestBody TimeIntervalData interval){
        Response<List<TimeIntervalData>> slots = visitsService.availableTimeSlots(interval.begin, interval.end);
        return slots.succeed()
                ? ResponseEntity.ok(slots.get())
                : ResponseToHttp.getFailureResponse(slots.getError());
    }

    @Scheduled(cron = "0 0 * * * *")
    public void automaticallyClosePastVisits() {
        var result = visitsService.updatePastVisitsStatusTo(Status.CLOSED_AUTOMATICALLY);
        ResponseEntity.ok().body(result);
    }

    private VisitRecord addLinksToEntity(VisitRecord visit) {
        visit.add(linkTo(VisitsController.class).slash(visit.getId()).withSelfRel());
        visit.add(linkTo(PetsController.class).slash(visit.pet.id).withRel("pet"));
        visit.add(linkTo(ClientsController.class).slash(visit.pet.owner.id).withRel("client"));
        return visit;
    }
}
