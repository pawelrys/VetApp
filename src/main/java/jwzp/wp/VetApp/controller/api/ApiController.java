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
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body(service.getAllVisits());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> UpdateVisit(@PathVariable int id, @RequestBody VisitData newData){
        return service.updateVisit(id, newData)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<?> AddVisit(@RequestBody VisitData visit){
        VisitRecord result = service.addVisit(visit);
        if(result != null){
            return ResponseEntity.ok(result.toString());
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}
