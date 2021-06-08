package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.PetsService;
import jwzp.wp.VetApp.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(path="/api/pets")
@RestController
public class PetsController {

    private final PetsService petsService;

    @Autowired
    public PetsController(PetsService petsService){
        this.petsService = petsService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPets() {
        List<PetRecord> pets = petsService.getAllPets();
        for (PetRecord pet : pets) {
            addLinksToEntity(pet);
        }
        Link link = linkTo(PetsController.class).withSelfRel();
        CollectionModel<PetRecord> result = CollectionModel.of(pets, link);
        return ResponseEntity.ok(result);
    }

//    @GetMapping(path = "/users/{clientId}/pets")
//    public ResponseEntity<?> getAllClientPets(@PathVariable int clientId) {
//        List<PetRecord> pets = petsService.getAllClientPets(clientId);
//        for (PetRecord pet : pets) {
//            addLinksToEntity(pet);
//        }
//        Link link = linkTo(PetsController.class).withSelfRel();
//        CollectionModel<PetRecord> result = CollectionModel.of(pets, link);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getPet(@PathVariable int id){
        Optional<PetRecord> pet = petsService.getPet(id);
        return pet.isPresent()
                ? ResponseEntity.ok(addLinksToEntity(pet.get()))
                : ResponseToHttp.getFailureResponse(ErrorMessagesBuilder.simpleError(ErrorType.PET_NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody PetData pet) {
        Response<PetRecord> result = petsService.addPet(pet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updatePet(@PathVariable int id, @RequestBody PetData newData) {
        Response<PetRecord> updated = petsService.updatePet(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(addLinksToEntity(updated.get()))
                : ResponseToHttp.getFailureResponse(updated.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deletePet(@PathVariable int id) {
        Response<PetRecord> result = petsService.delete(id);
        return result.succeed()
                ? ResponseEntity.ok(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    private PetRecord addLinksToEntity(PetRecord pet) {
        pet.add(linkTo(PetsController.class).slash(pet.id).withSelfRel());
        pet.add(linkTo(ClientsController.class).slash(pet.owner.id).withRel("owner"));
        return pet;
    }
}
