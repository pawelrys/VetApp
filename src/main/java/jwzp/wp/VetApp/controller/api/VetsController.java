package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import jwzp.wp.VetApp.service.VetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
            vet.add(linkTo(VetsController.class).slash(vet.id).withSelfRel());
        }
        Link self = linkTo(VetsController.class).withSelfRel();
        CollectionModel<VetRecord> result = CollectionModel.of(vets, self);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getVet(@PathVariable int id){
        Optional<VetRecord> vet = vetsService.getVet(id);
        if(vet.isPresent()){
            Link self = linkTo(VetsController.class).slash(vet.get().id).withSelfRel();
            EntityModel<VetRecord> result = EntityModel.of(vet.get(), self);
            return ResponseEntity.ok(result);
        } else {
            return ResponseToHttp.getFailureResponse(ResponseErrorMessage.VET_NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addVet(@RequestBody VetData vet) {
        Response<?> result = vetsService.addVet(vet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
