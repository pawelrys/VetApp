package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.resources.OfficesRepository;
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
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        OfficeRecord office = OfficeRecord.createOfficeRecord(requestedOffice);
        try {
            var savedOffice = repository.save(office);
            logger.info(LogsUtils.logSaved(savedOffice, savedOffice.id));
            return Response.succeedResponse(savedOffice);

        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Optional<OfficeRecord> getOffice(int id) {
        return repository.findById(id);
    }

    public boolean ableToCreateFromData(OfficeData office) {
        return office.name != null;
    }
}
