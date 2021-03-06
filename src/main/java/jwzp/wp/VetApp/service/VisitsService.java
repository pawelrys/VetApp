package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.utils.VetsTimeInterval;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.resources.OfficesRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import jwzp.wp.VetApp.resources.VetsRepository;
import jwzp.wp.VetApp.resources.VisitsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessageFormatter;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jwzp.wp.VetApp.service.Utils.Checker;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VisitsService {

    private final Logger logger = LogManager.getLogger(VisitsService.class);
    private final VisitsRepository visitsRepository;
    private final PetsRepository petsRepository;
    private final OfficesRepository officesRepository;
    private final VetsRepository vetsRepository;
    private final Clock clock;
    private final Duration TIME_TO_VISIT_GREATER_THAN = Duration.ofHours(1);

    @Autowired
    public VisitsService(
            VisitsRepository visitsRepository,
            PetsRepository petsRepository,
            OfficesRepository officesRepository,
            VetsRepository vetsRepository,
            Clock clock
    ) {
        this.visitsRepository = visitsRepository;
        this.petsRepository = petsRepository;
        this.officesRepository = officesRepository;
        this.vetsRepository = vetsRepository;
        this.clock = clock;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void automaticallyClosePastVisits() {
        var result = updatePastVisitsStatusTo(Status.CLOSED_AUTOMATICALLY);
        result.forEach( visitRecord -> {
                logger.info("Automatically closed: " + visitRecord.getClass() + " id: " + visitRecord.getId());
        });
    }

    public List<VisitRecord> getAllVisits() {
        return visitsRepository.findAll();
    }

    public List<VisitRecord> getAllVisitsByClient(int clientId) {
        return visitsRepository.getVisitsByClient(clientId);
    }

    public List<VisitRecord> getAllVisitsByVet(int vetId) {
        return visitsRepository.getVisitsByVet(vetId);
    }

    public Optional<VisitRecord> getVisit(int id) {
        return visitsRepository.findById(id);
    }

    public Response<VisitRecord> addVisit(VisitData requestedVisit) {
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(requestedVisit);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }
        // requestedVisit fields can't be null
        // we are checking it in Checker::getMissingData above
        try {
            var doesNotExists = new ErrorMessagesBuilder();
            Optional<PetRecord> pet = petsRepository.findById(requestedVisit.petId);
            if (pet.isEmpty()){
                logger.info(LogsUtils.logNotFoundObject(PetRecord.class, requestedVisit.petId));
                doesNotExists.addToMessage(ErrorMessageFormatter.doesNotExists(PetRecord.class, requestedVisit.petId));
            }
            Optional<OfficeRecord> office = officesRepository.findById(requestedVisit.officeId);
            if (office.isEmpty()){
                logger.info(LogsUtils.logNotFoundObject(OfficeRecord.class, requestedVisit.officeId));
                doesNotExists.addToMessage(ErrorMessageFormatter.doesNotExists(OfficeRecord.class, requestedVisit.officeId));
            }
            Optional<VetRecord> vet = vetsRepository.findById(requestedVisit.vetId);
            if (vet.isEmpty()){
                logger.info(LogsUtils.logNotFoundObject(VetRecord.class, requestedVisit.vetId));
                doesNotExists.addToMessage(ErrorMessageFormatter.doesNotExists(VetRecord.class, requestedVisit.vetId));
            }
            if (!doesNotExists.isEmpty()){
                return Response.errorResponse(doesNotExists.build(ErrorType.WRONG_ARGUMENTS));
            }

            Optional<ResponseErrorMessage> availabilityProblems = checkProblemsWithTimeAvailability(
                    requestedVisit.startDate,
                    requestedVisit.duration,
                    office.get(),
                    vet.get()
            );
            if (availabilityProblems.isPresent()) {
                logger.info(LogsUtils.logTimeUnavailability());
                return Response.errorResponse(availabilityProblems.get());
            }

            VisitRecord visit = VisitRecord.createVisitRecord(
                    requestedVisit.startDate,
                    requestedVisit.duration,
                    pet.get(),
                    requestedVisit.price,
                    office.get(),
                    vet.get(),
                    requestedVisit.description
            );
            var savedVisit = visitsRepository.save(visit);
            logger.info(LogsUtils.logSaved(savedVisit, savedVisit.getId()));
            return Response.succeedResponse(savedVisit);
        } catch (IllegalArgumentException e) {
            logger.error(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.INTERNAL_SERVICE_ERROR));
        }
    }

    public Response<VisitRecord> updateVisit(int id, VisitData newData) {
        Optional<VisitRecord> toUpdate = visitsRepository.findById(id);
        if (toUpdate.isEmpty()) {
            logger.info(LogsUtils.logNotFoundObject(VisitRecord.class, id));
            return Response.errorResponse(
                    ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND)
            );
        }
        var error = new ErrorMessagesBuilder();
        var newRecordOpt = createUpdatedVisit(toUpdate.get(), newData, error);
        if(newRecordOpt.isEmpty()) {
            return Response.errorResponse(error.build(ErrorType.WRONG_ARGUMENTS));
        }
        var newRecord = newRecordOpt.get();
        Optional<ResponseErrorMessage> result = checkProblemsWithTimeAvailability(
                newRecord.startDate,
                newRecord.duration,
                newRecord.office,
                newRecord.vet,
                id
        );
        if (result.isPresent()) {
            logger.info(LogsUtils.logTimeUnavailability());
            return Response.errorResponse(result.get());
        }
        var updatedVisit = visitsRepository.save(newRecord);
        logger.info(LogsUtils.logUpdated(updatedVisit, updatedVisit.getId()));
        return Response.succeedResponse(updatedVisit);
    }

    public Response<VisitRecord> delete(int id) {
        Optional<VisitRecord> visit = visitsRepository.findById(id);
        if (visit.isPresent()) {
            logger.info(LogsUtils.logDeleted(visit, id));
            visitsRepository.deleteById(visit.get().getId());
            return Response.succeedResponse(visit.get());
        }
        logger.info(LogsUtils.logNotFoundObject(VisitRecord.class, id));
        return Response.errorResponse(
                ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND)
        );
    }

    // split it and make descriptions
    private Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability(
            LocalDateTime start,
            Duration duration,
            OfficeRecord office,
            VetRecord vet,
            int id
    ) {
        if(vet.officeHoursStart.isAfter(start.toLocalTime())
                || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN))
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var end = start.plusMinutes(duration.toMinutes());
        var overlappedVisits = visitsRepository.getRegisteredVisitsInTime(start, end, office.id, vet.id);
        if(overlappedVisits.size() == 0 || (overlappedVisits.size() == 1 && id == overlappedVisits.get(0).getId())) {
            return Optional.empty();
        }
        VisitRecord record = overlappedVisits.get(0);
        if(record.getId() == id) {
            record = overlappedVisits.get(1);
        }
        return record.vet.id == vet.id
                ? Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET))
                : Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_OFFICE));
    }

    private Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability (
            LocalDateTime start,
            Duration duration,
            OfficeRecord office,
            VetRecord vet
    ) {
        if(vet.officeHoursStart.isAfter(start.toLocalTime())
                || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN))
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var end = start.plusMinutes(duration.toMinutes());
        var overlappedVisits = visitsRepository.getRegisteredVisitsInTime(start, end, office.id, vet.id);
        if(overlappedVisits.size() == 0) {
            return Optional.empty();
        }
        return overlappedVisits.get(0).vet.id == vet.id
                ? Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET))
                : Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_OFFICE));
    }

    private boolean isTimeToVisitGreaterThan(LocalDateTime start, Duration duration) {
        return Duration.between(LocalDateTime.now(clock), start).getSeconds() > duration.getSeconds();
    }

    public List<VisitRecord> updatePastVisitsStatusTo(Status status) {
        var records = visitsRepository.getPastVisitsWithStatus(LocalDateTime.now(clock), Status.PENDING);
        for (var visit : records) {
            changeStatusTo(visit, status);
        }
        return records;
    }

    private void changeStatusTo(VisitRecord visit, Status status) {
        VisitData data = new VisitData(
                visit.startDate,
                visit.duration,
                visit.pet.id,
                status,
                visit.price,
                visit.office.id,
                visit.vet.id,
                visit.description
        );
        var newRecord = createUpdatedVisit(visit, data);
        newRecord.ifPresent(visitsRepository::save);
    }

    public Response<List<VetsTimeInterval>> availableTimeSlots(
            LocalDateTime begin,
            LocalDateTime end,
            List<Integer> vetIds
    ) {
        if (begin == null || end == null) {
            logger.info("begin or end parameter is not provided");
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(
                    ErrorType.WRONG_ARGUMENTS,
                    "begin or end parameter is not provided"
            ));
        }
        if (begin.isAfter(end)) {
            logger.info("Begin of time interval is later than end");
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(
                    ErrorType.WRONG_ARGUMENTS,
                    "Begin of time interval is later than end"
            ));
        }
        Stream<VetsTimeInterval> availableSlots = visitsRepository.getAvailableTimeSlots(begin, end).stream();
        if (!vetIds.isEmpty()) {
            availableSlots = availableSlots.filter(t -> vetIds.containsAll(t.vetIds));
        }
        Map<Pair<LocalDateTime, LocalDateTime>, List<VetsTimeInterval>> slotsMapped = availableSlots
                .collect(Collectors.groupingBy((VetsTimeInterval t) -> Pair.of(t.begin, t.end)));

        List<VetsTimeInterval> timeSlots = slotsMapped.entrySet().stream()
                .map(t -> new VetsTimeInterval(
                        t.getKey().getFirst(),
                        t.getKey().getSecond(),
                        t.getValue().stream().map((VetsTimeInterval v) -> v.vetIds.get(0)).collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(a -> a.begin))
                .collect(Collectors.toList());

        return Response.succeedResponse(timeSlots);
    }

    public Optional<VisitRecord> createUpdatedVisit(VisitRecord thisVisit, VisitData data) {
        return createUpdatedVisit(thisVisit, data, null);
    }

    public Optional<VisitRecord> createUpdatedVisit(VisitRecord thisVisit, VisitData data, ErrorMessagesBuilder errorBuilder) {
        LocalDateTime startDate = (data.startDate != null) ? data.startDate : thisVisit.startDate;
        Duration duration = (data.duration != null) ? data.duration : thisVisit.duration;
        Optional<PetRecord> petRecord = (data.petId != null) ? petsRepository.findById(data.petId) : Optional.of(thisVisit.pet);
        Status status = (data.status != null) ? data.status : thisVisit.status;
        BigDecimal price = (data.price != null) ? data.price : thisVisit.price;
        Optional<OfficeRecord> officeRecord = (data.officeId != null) ? officesRepository.findById(data.officeId) : Optional.of(thisVisit.office);
        Optional<VetRecord> vetRecord = (data.vetId != null) ? vetsRepository.findById(data.vetId) : Optional.of(thisVisit.vet);
        String description = (!data.description.equals("")) ? data.description : thisVisit.description;
        if(petRecord.isPresent() && officeRecord.isPresent() && vetRecord.isPresent()) {
            return Optional.of(new VisitRecord(thisVisit.getId(), startDate, duration, petRecord.get(), status, price, officeRecord.get(), vetRecord.get(), description));
        } else if (errorBuilder != null) {
            if (petRecord.isEmpty()) {
                logger.info(LogsUtils.logNotFoundObject(PetRecord.class, data.petId));
                errorBuilder.addToMessage(ErrorMessageFormatter.doesNotExists(PetRecord.class, data.petId));
            }
            if (officeRecord.isEmpty()) {
                logger.info(LogsUtils.logNotFoundObject(OfficeRecord.class, data.officeId));
                errorBuilder.addToMessage(ErrorMessageFormatter.doesNotExists(OfficeRecord.class, data.officeId));
            }
            if (vetRecord.isEmpty()) {
                logger.info(LogsUtils.logNotFoundObject(VetRecord.class, data.vetId));
                errorBuilder.addToMessage(ErrorMessageFormatter.doesNotExists(VetRecord.class, data.vetId));
            }
        }
        return Optional.empty();
    }


}
