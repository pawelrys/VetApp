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
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public Optional<VisitRecord> getVisit(int id) {
        return visitsRepository.findById(id);
    }

    public Response<VisitRecord> addVisit(VisitData requestedVisit) {
        if (!ableToCreateFromData(requestedVisit)) {
            logger.info(LogsUtils.logMissingData(requestedVisit));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
        Optional<ResponseErrorMessage> result = checkProblemsWithTimeAvailability(requestedVisit.startDate, requestedVisit.duration, requestedVisit.officeId, requestedVisit.vetId);
        if (result.isPresent()) {
            logger.info(LogsUtils.logTimeUnavailability());
            return Response.errorResponse(result.get());
        }
        try {
            PetRecord pet = petsRepository.findById(requestedVisit.petId).orElseThrow();
            OfficeRecord office = officesRepository.findById(requestedVisit.officeId).orElseThrow();
            VetRecord vet = vetsRepository.findById(requestedVisit.vetId).orElseThrow();
            VisitRecord visit = VisitRecord.createNewVisit(
                    requestedVisit.startDate,
                    requestedVisit.duration,
                    pet,
                    requestedVisit.price,
                    office,
                    vet
            );
            var savedVisit = visitsRepository.save(visit);
            logger.info(LogsUtils.logSaved(savedVisit, savedVisit.getId()));
            return Response.succeedResponse(savedVisit);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
    }

    public Response<VisitRecord> updateVisit(int id, VisitData newData) {
        Optional<VisitRecord> toUpdate = visitsRepository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            Optional<ResponseErrorMessage> result = checkProblemsWithTimeAvailability(toUpdate.get().startDate, toUpdate.get().duration, toUpdate.get().office.id, toUpdate.get().vet.id, id);
            if (result.isPresent()) {
                logger.info(LogsUtils.logTimeUnavailability());
                return Response.errorResponse(result.get());
            }
            var updatedVisit = visitsRepository.save(toUpdate.get());
            logger.info(LogsUtils.logUpdated(updatedVisit, updatedVisit.getId()));
            return Response.succeedResponse(toUpdate.get());
        }
        logger.info(LogsUtils.logNotFoundObject(VisitRecord.class, id));
        return Response.errorResponse(
                ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND)
        );
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

    private Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability(LocalDateTime start, Duration duration, Integer officeId, Integer vetId, int id) {
        VetRecord vet = vetsRepository.findById(vetId).orElseThrow();
        if(vet.officeHoursStart.isAfter(start.toLocalTime()) || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN)) return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var end = start.plusMinutes(duration.toMinutes());
        var overlappedVisits = visitsRepository.getRegisteredVisitsInTime(start, end, officeId, vetId);
        if(overlappedVisits.size() == 0 || (overlappedVisits.size() == 1 && id == overlappedVisits.get(0).getId())) {
            return Optional.empty();
        }
        VisitRecord record = overlappedVisits.get(0);
        if(record.getId() == id) {
            record = overlappedVisits.get(1);
        }
        return record.vet.id == vetId
                ? Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET))
                : Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_OFFICE));
    }

    private Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability(LocalDateTime start, Duration duration, Integer officeId, Integer vetId) {
        VetRecord vet = vetsRepository.findById(vetId).orElseThrow();
        if(vet.officeHoursStart.isAfter(start.toLocalTime()) || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN)) return Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var end = start.plusMinutes(duration.toMinutes());
        var overlappedVisits = visitsRepository.getRegisteredVisitsInTime(start, end, officeId, vetId);
        if(overlappedVisits.size() == 0) {
            return Optional.empty();
        }
        return overlappedVisits.get(0).vet.id == vetId
                ? Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET))
                : Optional.of(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_OFFICE));
    }

    public boolean ableToCreateFromData(VisitData visit) {
        return visit.petId != null
                && visit.duration != null
                && visit.price != null
                && visit.startDate != null
                && visit.officeId != null
                && visit.vetId != null;
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
        VisitData data = new VisitData(visit.startDate, visit.duration, visit.pet.id, status, visit.price, visit.office.id, visit.vet.id);
        visit.update(data);
        visitsRepository.save(visit);
    }

    public Response<List<VetsTimeInterval>> availableTimeSlots(VetsTimeInterval input){
        if (input.begin == null || input.end == null || input.begin.isAfter(input.end)) {
            logger.info(LogsUtils.logMissingData(input));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
        List<Object[]> availableSlots = visitsRepository.getAvailableTimeSlots(input.begin, input.end);
        Map<Pair<LocalDateTime, LocalDateTime>, List<Object[]>> slotsMapped = availableSlots.stream()
                .filter(t -> input.vetIds.contains((Integer)t[2]))
                .collect(Collectors.groupingBy(
                        t -> Pair.of(
                                ((Timestamp)t[0]).toLocalDateTime(),
                                ((Timestamp)t[1]).toLocalDateTime()
                        )
                ));
        List<VetsTimeInterval> timeSlots = slotsMapped.entrySet().stream()
                .map(t -> new VetsTimeInterval(
                        t.getKey().getFirst(),
                        t.getKey().getSecond(),
                        t.getValue().stream().map(v -> (Integer)v[2]).collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(a -> a.begin))
                .collect(Collectors.toList());

        return Response.succeedResponse(timeSlots);
    }
}
