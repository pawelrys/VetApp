package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.service.OfficesService;
import jwzp.wp.VetApp.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/api/offices")
@RestController
public class OfficesController {

    private final OfficesService officesService;

    @Autowired
    public OfficesController(OfficesService officesService) {
        this.officesService = officesService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOffices() {
        return ResponseEntity.ok().body(officesService.getAllOffices());
    }

    @PostMapping
    public ResponseEntity<?> addOffice(@RequestBody OfficeData office) {
        Response<?> result = officesService.addOffice(office);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
