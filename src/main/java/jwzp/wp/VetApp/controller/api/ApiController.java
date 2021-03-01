package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.service.VisitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping(path="/api/visits")
@RestController
public class ApiController {

    private final VisitsService service;

    @Autowired
    private ApiController(VisitsService service){
        this.service = service;
    }

    @GetMapping(path="/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping
    public ResponseEntity<?> getAllVisits(){
        return ResponseEntity.ok().body(service.getAllVisits());
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVisit(@PathVariable int id){
        VisitRecord visit = service.getVisit(id);
        return visit != null
                ? ResponseEntity.ok(visit)
                : ResponseEntity.badRequest().body("not found visit with id: " + id);
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> UpdateVisit(@PathVariable int id, @RequestBody VisitData newData){
        VisitRecord updated = service.updateVisit(id, newData);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> AddVisit(@RequestBody VisitData visit){
        VisitRecord result = service.addVisit(visit);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
