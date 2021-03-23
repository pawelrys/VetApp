package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClientsService {

    private final ClientsRepository repository;

    @Autowired
    public ClientsService(ClientsRepository repository) {
        this.repository = repository;
    }

    public List<ClientRecord> getAllClients() {
        return repository.findAll();
    }

    public Response<?> addClient(ClientData requestedClient) {
        if (!ableToCreateFromData(requestedClient)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        ClientRecord client = ClientRecord.createClientRecord(requestedClient);
        try {
            return Response.succeedResponse(repository.save(client));
        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(ClientData client) {
        return client.name != null && client.surname != null;
    }
}
