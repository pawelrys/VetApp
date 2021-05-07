package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VetsService {

    private final Logger logger = LogManager.getLogger(VetsService.class);
    private final VetsRepository repository;

    @Autowired
    public VetsService(VetsRepository repository) {
        this.repository = repository;
    }

    public List<VetRecord> getAllVets() {
        return repository.findAll();
    }

    public Optional<VetRecord> getVet(int id){
        return repository.findById(id);
    }

    public Response<VetRecord> addVet(VetData requestedVet) {
        if (!ableToCreateFromData(requestedVet)) {
            logger.info(LogsUtils.logMissingData(requestedVet));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        VetRecord vet = VetRecord.createVetRecord(requestedVet);
        try {
            var savedVet = repository.save(vet);
            logger.info(LogsUtils.logSaved(savedVet, savedVet.id));
            return Response.succeedResponse(savedVet);
        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Response<VetRecord> updateVet(int id, VetData newData) {
        Optional<VetRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            var saved = repository.save(toUpdate.get());
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(toUpdate.get());
        }
        logger.info(LogsUtils.logNotFoundObject(VetRecord.class, id));
        return Response.errorResponse(ResponseErrorMessage.VET_NOT_FOUND);
    }

    public Response<VetRecord> deleteVet(int id) {
        Optional<VetRecord> toDelete = repository.findById(id);
        if (toDelete.isPresent()) {
            VetRecord vet = toDelete.get();
            repository.deleteById(vet.id);
            logger.info(LogsUtils.logDeleted(vet, vet.id));
            return Response.succeedResponse(vet);
        }
        logger.info(LogsUtils.logNotFoundObject(VetRecord.class, id));
        return Response.errorResponse(ResponseErrorMessage.VET_NOT_FOUND);
    }

    public boolean ableToCreateFromData(VetData vet) {
        return vet.name != null && vet.surname != null && vet.officeHoursEnd != null && vet.officeHoursStart != null && vet.photo != null;
    }
}
