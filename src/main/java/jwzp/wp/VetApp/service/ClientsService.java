package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClientsService {

    private final Logger logger = LogManager.getLogger(ClientsService.class);
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
            logger.info(LogsUtils.logMissingData(requestedClient));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        ClientRecord client = ClientRecord.createClientRecord(requestedClient);
        try {
            var savedClient = repository.save(client);
            logger.info(LogsUtils.logSaved(savedClient, savedClient.id));
            return Response.succeedResponse(savedClient);
        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Response<ClientRecord> updateClient(int id, ClientData newData) {
        Optional<ClientRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            var saved = repository.save(toUpdate.get());
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(toUpdate.get());
        }
        logger.info(LogsUtils.logNotFoundObject(ClientRecord.class, id));
        return Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    public Response<ClientRecord> delete(int id) {
        Optional<ClientRecord> clientOpt = repository.findById(id);
        if (clientOpt.isPresent()) {
            ClientRecord client = clientOpt.get();
            repository.deleteById(client.id);
            logger.info(LogsUtils.logDeleted(client, client.id));
            return Response.succeedResponse(client);
        }
        logger.info(LogsUtils.logNotFoundObject(ClientRecord.class, id));
        return Response.errorResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
    }

    public boolean ableToCreateFromData(ClientData client) {
        return client.name != null && client.surname != null;
    }
}
