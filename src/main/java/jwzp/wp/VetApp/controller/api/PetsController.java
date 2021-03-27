package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.service.PetsService;
import jwzp.wp.VetApp.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().body(petsService.getAllPets());
    }

    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody PetData pet) {
        Response<?> result = petsService.addPet(pet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
