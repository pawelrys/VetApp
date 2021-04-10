package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
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

@RequestMapping(path="/api/clients")
@RestController
public class ClientsController {

    private final ClientsService clientsService;

    @Autowired
    public ClientsController(ClientsService clientsService){
        this.clientsService = clientsService;
    }

    @GetMapping(path="/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        List<ClientRecord> clients = clientsService.getAllClients();
        for(var client : clients){
            client.add(linkTo(ClientsController.class).slash(client.id).withSelfRel());
        }
        Link link = linkTo(ClientsController.class).withSelfRel();
        CollectionModel<ClientRecord> result = CollectionModel.of(clients, link);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getClient(@PathVariable int id) {
        Optional<ClientRecord> client = clientsService.getClient(id);
        if (client.isPresent()) {
            Link link = linkTo(ClientsController.class).slash(client.get().id).withSelfRel();
            EntityModel<ClientRecord> result = EntityModel.of(client.get(), link);
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseToHttp.getFailureResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addClient(@RequestBody ClientData client) {
        Response<?> result = clientsService.addClient(client);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
