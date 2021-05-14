package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(path="/api/clients")
@RestController
public class ClientsController {

    private final ClientsService clientsService;

    @Autowired
    public ClientsController(ClientsService clientsService){
        this.clientsService = clientsService;
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        List<ClientRecord> clients = clientsService.getAllClients();
        for(var client : clients){
            addLinksToEntity(client);
        }
        Link link = linkTo(ClientsController.class).withSelfRel();
        CollectionModel<ClientRecord> result = CollectionModel.of(clients, link);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getClient(@PathVariable int id) {
        Optional<ClientRecord> client = clientsService.getClient(id);
        return client.isPresent()
                ? ResponseEntity.ok(addLinksToEntity(client.get()))
                : ResponseToHttp.getFailureResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> addClient(@RequestBody ClientData client) {
        Response<ClientRecord> result = clientsService.addClient(client);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    @PatchMapping(path="/{id}")
    public ResponseEntity<?> updateClient(@PathVariable int id, @RequestBody ClientData newData) {
        Response<ClientRecord> updated = clientsService.updateClient(id, newData);
        return updated.succeed()
                ? ResponseEntity.ok(addLinksToEntity(updated.get()))
                : ResponseToHttp.getFailureResponse(updated.getError());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable int id) {
        Response<ClientRecord> result = clientsService.delete(id);
        return result.succeed()
                ? ResponseEntity.ok(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    private ClientRecord addLinksToEntity(ClientRecord client) {
        return client.add(linkTo(ClientsController.class).slash(client.id).withSelfRel());
    }
}
