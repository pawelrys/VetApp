package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.resources.OfficesRepository;
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
public class OfficesService {

    private final Logger logger = LogManager.getLogger(OfficesService.class);
    private final OfficesRepository repository;

    @Autowired
    public OfficesService(OfficesRepository repository) {
        this.repository = repository;
    }

    public List<OfficeRecord> getAllOffices() {
        return repository.findAll();
    }

    public Response<OfficeRecord> addOffice(OfficeData requestedOffice) {
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(requestedOffice);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }
        OfficeRecord office = OfficeRecord.createOfficeRecord(requestedOffice.name);
        try {
            var savedOffice = repository.save(office);
            logger.info(LogsUtils.logSaved(savedOffice, savedOffice.id));
            return Response.succeedResponse(savedOffice);

        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
    }

    public Response<OfficeRecord> updateOffice(int id, OfficeData newData) {
        Optional<OfficeRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            var newRecord = createUpdatedOffice(toUpdate.get(), newData);
            var saved = repository.save(newRecord);
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(newRecord);
        }
        logger.info(LogsUtils.logNotFoundObject(OfficeRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.OFFICE_NOT_FOUND));
    }

    public Response<OfficeRecord> delete(int id) {
        Optional<OfficeRecord> officeOpt = repository.findById(id);
        if (officeOpt.isPresent()) {
            OfficeRecord office = officeOpt.get();
            repository.deleteById(office.id);
            logger.info(LogsUtils.logDeleted(office, office.id));
            return Response.succeedResponse(office);
    }
        logger.info(LogsUtils.logNotFoundObject(OfficeRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.OFFICE_NOT_FOUND));
    }

    public Optional<OfficeRecord> getOffice(int id) {
        return repository.findById(id);
    }

    public OfficeRecord createUpdatedOffice(OfficeRecord thisOffice, OfficeData data) {
        String name = (data.name != null) ? data.name : thisOffice.name;
        return new OfficeRecord(thisOffice.id, name);
    }
}
