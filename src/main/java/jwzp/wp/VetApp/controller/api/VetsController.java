package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.VetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().body(vetsService.getAllVets());
    }

    @PostMapping
    public ResponseEntity<?> addVet(@RequestBody VetData vet) {
        Response<?> result = vetsService.addVet(vet);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
