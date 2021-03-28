package jwzp.wp.VetApp.controller.api;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().body(clientsService.getAllClients());
    }

    @PostMapping
    public ResponseEntity<?> addClient(@RequestBody ClientData client) {
        Response<?> result = clientsService.addClient(client);
        return result.succeed()
                ? ResponseEntity.status(HttpStatus.CREATED).body(result.get())
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
