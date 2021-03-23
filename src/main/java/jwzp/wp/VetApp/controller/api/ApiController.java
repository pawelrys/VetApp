package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping(path="/api/visits")
@RestController
public class ApiController {

    private final VisitsService visitsService;
    private final ClientsService clientService;
    private final VetsService vetsService;
    private final PetsService petsService;

    @Autowired
    private ApiController(VisitsService visitsService, ClientsService clientService, VetsService vetsService, PetsService petsService) {
        this.visitsService = visitsService;
        this.clientService = clientService;
        this.vetsService = vetsService;
        this.petsService = petsService;
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

    @GetMapping(path = "/clients")
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok().body(clientService.getAllClients());
    }

    @PostMapping(path = "/clients")
    public ResponseEntity<?> addClient(@RequestBody ClientData client) {
        Response<?> result = clientService.addClient(client);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @GetMapping(path = "/pets")
    public ResponseEntity<?> getAllPets() {
        return ResponseEntity.ok().body(petsService.getAllPets());
    }

    @PostMapping(path = "/pets")
    public ResponseEntity<?> addPet(@RequestBody PetData pet) {
        Response<?> result = petsService.addPet(pet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @GetMapping(path = "/vets")
    public ResponseEntity<?> getAllVets() {
        return ResponseEntity.ok().body(vetsService.getAllVets());
    }

    @PostMapping(path = "/vets")
    public ResponseEntity<?> addVet(@RequestBody VetData vet) {
        Response<?> result = vetsService.addVet(vet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
