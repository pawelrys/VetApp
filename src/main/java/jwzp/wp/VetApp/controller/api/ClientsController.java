package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(path="/api/clients")
@RestController
public class ClientsController {

    private final ClientsService clientsService;
    private final Logger logger = LoggerFactory.getLogger(ClientsController.class);

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
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getClient(@PathVariable int id) {
        Optional<ClientRecord> client = clientsService.getClient(id);
        return client.isPresent()
                ? ResponseEntity.ok().body(addLinksToEntity(client.get()))
                : ResponseToHttp.getFailureResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> addClient(@RequestBody ClientData client) {
        Response<ClientRecord> result = clientsService.addClient(client);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(addLinksToEntity(result.get()))
                : ResponseToHttp.getFailureResponse(result.getError());
    }

    private ClientRecord addLinksToEntity(ClientRecord client) {
        return client.add(linkTo(ClientsController.class).slash(client.id).withSelfRel());
    }
}
