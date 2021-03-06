package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import jwzp.wp.VetApp.service.Utils.Checker;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(requestedVet);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }
        VetRecord vet = VetRecord.createVetRecord(requestedVet.name, requestedVet.surname, requestedVet.photo, requestedVet.officeHoursStart, requestedVet.officeHoursEnd);
        try {
            var savedVet = repository.save(vet);
            logger.info(LogsUtils.logSaved(savedVet, savedVet.id));
            return Response.succeedResponse(savedVet);
        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
    }

    public Response<VetRecord> updateVet(int id, VetData newData) {
        Optional<VetRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            var newRecord = createUpdatedVet(toUpdate.get(), newData);
            var saved = repository.save(newRecord);
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(newRecord);
        }
        logger.info(LogsUtils.logNotFoundObject(VetRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VET_NOT_FOUND));
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
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VET_NOT_FOUND));
    }

    public VetRecord createUpdatedVet(VetRecord thisVet, VetData data) {
        String name = (data.name != null) ? data.name : thisVet.name;
        String surname = (data.surname != null) ? data.surname : thisVet.surname;
        byte[] photo = (data.photo != null) ? data.photo : thisVet.photo;
        LocalTime officeHoursStart = (data.officeHoursStart != null) ? data.officeHoursStart : thisVet.officeHoursStart;
        LocalTime officeHoursEnd = (data.officeHoursEnd != null) ? data.officeHoursEnd : thisVet.officeHoursEnd;
        return new VetRecord(thisVet.id, name, surname, photo, officeHoursStart, officeHoursEnd);
    }
}
