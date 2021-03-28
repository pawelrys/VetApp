package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitsService {

    private final VisitsRepository visitsRepository;
    private final PetsRepository petsRepository;
    private final Duration TIME_TO_VISIT_GREATER_THAN = Duration.ofHours(1);

    @Autowired
    private VisitsService(VisitsRepository visitsRepository, PetsRepository petsRepository) {
        this.visitsRepository = visitsRepository;
        this.petsRepository = petsRepository;
    }

    public List<VisitRecord> getAllVisits() {
        return visitsRepository.findAll();
    }

    public Optional<VisitRecord> getVisit(int id) {
        return visitsRepository.findById(id);
    }

    public Response<?> addVisit(VisitData requestedVisit) {
        if (!ableToCreateFromData(requestedVisit)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        if (!isTimeAvailable(requestedVisit.startDate, requestedVisit.duration)) {
            return Response.errorResponse(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
        }
        try {
            PetRecord pet = petsRepository.findById(requestedVisit.petId).orElseThrow();
            VisitRecord visit = VisitRecord.createNewVisit(
                    requestedVisit.startDate,
                    requestedVisit.duration,
                    pet,
                    requestedVisit.price
            );
            return Response.succeedResponse(visitsRepository.save(visit));
        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Response<?> updateVisit(int id, VisitData newData) {
        Optional<VisitRecord> toUpdate = visitsRepository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            if (!isTimeAvailable(
                    toUpdate.get().startDate,
                    toUpdate.get().duration
            )) {
                return Response.errorResponse(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
            }
            visitsRepository.save(toUpdate.get());
            return Response.succeedResponse(toUpdate.get());
        }
        return Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    public Response<?> delete(int id) {
        Optional<VisitRecord> visit = visitsRepository.findById(id);
        if (visit.isPresent()) {
            visitsRepository.deleteById(visit.get().getId());
            return Response.succeedResponse(visit.get());
        }
        return Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    public boolean isTimeAvailable(LocalDateTime start, Duration duration) {
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN)) return false;
        var end = start.plusMinutes(duration.toMinutes());
        return visitsRepository.getRecordsInTime(start, end).size() == 0;
    }

    public boolean ableToCreateFromData(VisitData visit) {
        return visit.petId != null && visit.duration != null && visit.price != null && visit.startDate != null;
    }

    public boolean isTimeToVisitGreaterThan(LocalDateTime start, Duration duration) {
        return Duration.between(LocalDateTime.now(), start).getSeconds() > duration.getSeconds();
    }

    public List<VisitRecord> updateVisitStatusTo(Status status) {
        var records = repository.getFinishedVisitsWithStatus(LocalDateTime.now(), Status.PENDING);
        for (var visit : records) {
            changeStatusTo(visit, status);
        }
        return records;
    }

    public void changeStatusTo(VisitRecord visit, Status status) {
        VisitData data = new VisitData(visit.startDate, visit.duration, visit.animalKind, status, visit.price);
        updateVisit(visit.getId(), data);
    }
}
