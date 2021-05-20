package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import jwzp.wp.VetApp.service.Utils.Checker;
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
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(requestedClient);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }
        ClientRecord client = ClientRecord.createClientRecord(requestedClient.name, requestedClient.surname);
        try {
            var savedClient = repository.save(client);
            logger.info(LogsUtils.logSaved(savedClient, savedClient.id));
            return Response.succeedResponse(savedClient);
        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
    }

    public Response<ClientRecord> updateClient(int id, ClientData newData) {
        Optional<ClientRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            var newRecord = createUpdatedClient(toUpdate.get(), newData);
            var saved = repository.save(newRecord);
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(newRecord);
        }
        logger.info(LogsUtils.logNotFoundObject(ClientRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.CLIENT_NOT_FOUND));
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
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.CLIENT_NOT_FOUND));
    }

    public boolean ableToCreateFromData(ClientData client) {
        return client.name != null && client.surname != null;
    }
    public ClientRecord createUpdatedClient(ClientRecord thisClient, ClientData data) {
        String name = (data.name != null) ? data.name : thisClient.name;
        String surname = (data.surname != null) ? data.surname : thisClient.surname;
        return new ClientRecord(thisClient.id, name, surname);
    }

}
