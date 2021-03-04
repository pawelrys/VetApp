package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitsService {

    private final VisitsRepository repository;

    @Autowired
    private VisitsService(VisitsRepository repository){
        this.repository = repository;
    }

    public List<VisitRecord> getAllVisits(){
        return repository.findAll();
    }

    public Optional<VisitRecord> getVisit(int id){
        return repository.findById(id);
    }

    public Optional<VisitRecord> addVisit(VisitData requestedVisit) {
        if(ableToCreateFromData(requestedVisit)) return Optional.empty();
        if (!isTimeAvailable(requestedVisit.startDate, requestedVisit.duration)) {
            return Optional.empty();
        }
        VisitRecord visit = VisitRecord.createNewVisit(requestedVisit);
        try {
            return Optional.of(repository.save(visit));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<VisitRecord> updateVisit(int id, VisitData newData){
        Optional<VisitRecord> toUpdate = repository.findById(id);
        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            repository.save(toUpdate.get());
        }
        return toUpdate;
    }

    public Optional<VisitRecord> delete(int id) {
        Optional<VisitRecord> visit = repository.findById(id);
        visit.ifPresent(visitRecord -> repository.deleteById(visitRecord.getId()));
        return visit;
    }

    public boolean isTimeAvailable(LocalDateTime start, Duration duration) {
        var end = start.plusMinutes(duration.toMinutes());
        return repository.getRecordsInTime(start, end).size() == 0;
    }

    public boolean ableToCreateFromData(VisitData visit) {
        return visit.animalKind != null && visit.duration != null && visit.price != null && visit.startDate != null;
    }
}
