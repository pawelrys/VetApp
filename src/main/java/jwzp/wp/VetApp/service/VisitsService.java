package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.utils.TimeIntervalData;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.resources.OfficesRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import jwzp.wp.VetApp.resources.VetsRepository;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VisitsService {

    private final VisitsRepository visitsRepository;
    private final PetsRepository petsRepository;
    private final OfficesRepository officesRepository;
    private final VetsRepository vetsRepository;
    private final Duration TIME_TO_VISIT_GREATER_THAN = Duration.ofHours(1);

    @Autowired
    private VisitsService(VisitsRepository visitsRepository, PetsRepository petsRepository, OfficesRepository officesRepository, VetsRepository vetsRepository) {
        this.visitsRepository = visitsRepository;
        this.petsRepository = petsRepository;
        this.officesRepository = officesRepository;
        this.vetsRepository = vetsRepository;
    }

    public List<VisitRecord> getAllVisits() {
        return visitsRepository.findAll();
    }

    public Optional<VisitRecord> getVisit(int id) {
        return visitsRepository.findById(id);
    }

    public Response<VisitRecord> addVisit(VisitData requestedVisit) {
        if (!ableToCreateFromData(requestedVisit)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        Optional<ResponseErrorMessage> result = checkProblemsWithTimeAvailability(requestedVisit.startDate, requestedVisit.duration, requestedVisit.officeId, requestedVisit.vetId);
        if (result.isPresent()) {
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
            return Response.succeedResponse(visitsRepository.save(visit));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public Response<VisitRecord> updateVisit(int id, VisitData newData) {
        Optional<VisitRecord> toUpdate = visitsRepository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            Optional<ResponseErrorMessage> result = checkProblemsWithTimeAvailability(toUpdate.get().startDate, toUpdate.get().duration, toUpdate.get().office.id, toUpdate.get().vet.id, id);
            if (result.isPresent()) {
                return Response.errorResponse(result.get());
            }
            visitsRepository.save(toUpdate.get());
            return Response.succeedResponse(toUpdate.get());
        }
        return Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    public Response<VisitRecord> delete(int id) {
        Optional<VisitRecord> visit = visitsRepository.findById(id);
        if (visit.isPresent()) {
            visitsRepository.deleteById(visit.get().getId());
            return Response.succeedResponse(visit.get());
        }
        return Response.errorResponse(ResponseErrorMessage.VISIT_NOT_FOUND);
    }

    public Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability(LocalDateTime start, Duration duration, Integer officeId, Integer vetId, int id) {
        VetRecord vet = vetsRepository.findById(vetId).orElseThrow();
        if(vet.officeHoursStart.isAfter(start.toLocalTime()) || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ResponseErrorMessage.BUSY_VET);
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN)) return Optional.of(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
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
                ? Optional.of(ResponseErrorMessage.BUSY_VET)
                : Optional.of(ResponseErrorMessage.BUSY_OFFICE);
    }

    public Optional<ResponseErrorMessage> checkProblemsWithTimeAvailability(LocalDateTime start, Duration duration, Integer officeId, Integer vetId) {
        VetRecord vet = vetsRepository.findById(vetId).orElseThrow();
        if(vet.officeHoursStart.isAfter(start.toLocalTime()) || vet.officeHoursEnd.isBefore(start.toLocalTime().plus(duration))) {
            return Optional.of(ResponseErrorMessage.BUSY_VET);
        }
        if(!isTimeToVisitGreaterThan(start, TIME_TO_VISIT_GREATER_THAN)) return Optional.of(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
        var end = start.plusMinutes(duration.toMinutes());
        var overlappedVisits = visitsRepository.getRegisteredVisitsInTime(start, end, officeId, vetId);
        if(overlappedVisits.size() == 0) {
            return Optional.empty();
        }
        return overlappedVisits.get(0).vet.id == vetId
                ? Optional.of(ResponseErrorMessage.BUSY_VET)
                : Optional.of(ResponseErrorMessage.BUSY_OFFICE);
    }

    public boolean ableToCreateFromData(VisitData visit) {
        return visit.petId != null && visit.duration != null && visit.price != null && visit.startDate != null;
    }

    public boolean isTimeToVisitGreaterThan(LocalDateTime start, Duration duration) {
        return Duration.between(LocalDateTime.now(), start).getSeconds() > duration.getSeconds();
    }

    public List<VisitRecord> updatePastVisitsStatusTo(Status status) {
        var records = visitsRepository.getPastVisitsWithStatus(LocalDateTime.now(), Status.PENDING);
        for (var visit : records) {
            changeStatusTo(visit, status);
        }
        return records;
    }

    public void changeStatusTo(VisitRecord visit, Status status) {
        VisitData data = new VisitData(visit.startDate, visit.duration, visit.pet.id, status, visit.price, visit.office.id, visit.vet.id);
        updateVisit(visit.getId(), data);
    }

    public Response<List<TimeIntervalData>> availableTimeSlots(LocalDateTime start, LocalDateTime end){
        if (start == null || end == null || start.isAfter(end)){
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        return Response.succeedResponse(visitsRepository.getAvailableTimeSlots(start, end).stream()
                .map(t -> new TimeIntervalData(t[0].toLocalDateTime(), t[1].toLocalDateTime()))
                .collect(Collectors.toList()));
    }
}
