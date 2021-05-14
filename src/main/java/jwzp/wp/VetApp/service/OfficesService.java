package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.resources.OfficesRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
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
        if(!ableToCreateFromData(requestedOffice)) {
            logger.info(LogsUtils.logMissingData(requestedOffice));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        OfficeRecord office = OfficeRecord.createOfficeRecord(requestedOffice);
        try {
            var savedOffice = repository.save(office);
            logger.info(LogsUtils.logSaved(savedOffice, savedOffice.id));
            return Response.succeedResponse(savedOffice);

        } catch (IllegalArgumentException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Response<OfficeRecord> updateOffice(int id, OfficeData newData) {
        Optional<OfficeRecord> toUpdate = repository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            var saved = repository.save(toUpdate.get());
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(toUpdate.get());
        }
        logger.info(LogsUtils.logNotFoundObject(OfficeRecord.class, id));
        return Response.errorResponse(ResponseErrorMessage.OFFICE_NOT_FOUND);
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
        return Response.errorResponse(ResponseErrorMessage.OFFICE_NOT_FOUND);
    }

    public Optional<OfficeRecord> getOffice(int id) {
        return repository.findById(id);
    }

    public boolean ableToCreateFromData(OfficeData office) {
        return office.name != null;
    }
}
