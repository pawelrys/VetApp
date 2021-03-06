package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.OfficesService;
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
        List<OfficeRecord> offices = officesService.getAllOffices();
        for(var office : offices) {
            addLinksToEntity(office);
        }
        Link self = linkTo(OfficesController.class).withSelfRel();
        CollectionModel<OfficeRecord> result = CollectionModel.of(offices, self);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getOffice(@PathVariable int id) {
        Optional<OfficeRecord> office = officesService.getOffice(id);
        return office.isPresent()
                ? ResponseEntity.ok(addLinksToEntity(office.get()))
                : ResponseToHttp.getFailureResponse(ErrorMessagesBuilder.simpleError(ErrorType.OFFICE_NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> addOffice(@RequestBody OfficeData office) {
        Response<OfficeRecord> result = officesService.addOffice(office);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }


    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable int id, @RequestBody OfficeData newData) {
        Response<OfficeRecord> updated = officesService.updateOffice(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(addLinksToEntity(updated.get()))
                : ResponseToHttp.getFailureResponse(updated.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable int id) {
        Response<OfficeRecord> result = officesService.delete(id);
        return result.succeed()
                ? ResponseEntity.ok(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    private OfficeRecord addLinksToEntity(OfficeRecord office){
        return office.add(linkTo(OfficesController.class).slash(office.id).withSelfRel());
    }
}
