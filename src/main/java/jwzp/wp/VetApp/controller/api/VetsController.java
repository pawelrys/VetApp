package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import jwzp.wp.VetApp.service.VetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(path="/api/vets")
@RestController
public class VetsController {

    private final VetsService vetsService;

    @Autowired
    public VetsController(VetsService vetsService){
        this.vetsService = vetsService;
    }

    @GetMapping
    public ResponseEntity<?> getAllVets() {
        List<VetRecord> vets = vetsService.getAllVets();
        for(var vet : vets){
            addLinksToEntity(vet);
        }
        Link self = linkTo(VetsController.class).withSelfRel();
        CollectionModel<VetRecord> result = CollectionModel.of(vets, self);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVet(@PathVariable int id){
        Optional<VetRecord> vet = vetsService.getVet(id);
        return vet.isPresent()
                ? ResponseEntity.ok().body(addLinksToEntity(vet.get()))
                : ResponseToHttp.getFailureResponse(ResponseErrorMessage.VET_NOT_FOUND);

    }

    @PostMapping
    public ResponseEntity<?> addVet(@RequestBody VetData vet) {
        Response<VetRecord> result = vetsService.addVet(vet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable int id, @RequestBody VetData newData) {
        Response<VetRecord> updated = vetsService.updateVet(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(addLinksToEntity(updated.get()))
                : ResponseToHttp.getFailureResponse(updated.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable int id) {
        Response<VetRecord> result = vetsService.deleteVet(id);
        return result.succeed()
                ? ResponseEntity.ok(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    private VetRecord addLinksToEntity(VetRecord vet) {
        return vet.add(linkTo(VetsController.class).slash(vet.id).withSelfRel());
    }
}
