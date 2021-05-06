package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClientsService {

    private final Logger logger = LoggerFactory.getLogger(ClientsService.class);
    private final ClientsRepository repository;

    @Autowired
    public ClientsService(ClientsRepository repository) {
        this.repository = repository;
    }

    public List<ClientRecord> getAllClients() {
        return repository.findAll();
    }

    public Optional<ClientRecord> getClient(int id) {
        return repository.findById(id);
    }

    public Response<ClientRecord> addClient(ClientData requestedClient) {
        if (!ableToCreateFromData(requestedClient)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        ClientRecord client = ClientRecord.createClientRecord(requestedClient);
        try {
            var savedClient = repository.save(client);
            logger.info(LogsUtils.logSaved(savedClient, savedClient.id));
            return Response.succeedResponse(savedClient);
        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(ClientData client) {
        return client.name != null && client.surname != null;
    }
}
