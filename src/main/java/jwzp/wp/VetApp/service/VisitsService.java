package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    public VisitRecord getVisit(int id){
        return repository.findById(id).orElse(null);
    }

    public VisitRecord addVisit(VisitData requestedVisit){
        if (!isTimeAvailable(requestedVisit.startDate, requestedVisit.duration)) {
            return null;
        }
        VisitRecord visit = VisitRecord.createNewVisit(requestedVisit);
        try {
            return repository.save(visit);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public VisitRecord updateVisit(int id, VisitData newData){
        VisitRecord toUpdate = repository.findById(id).orElse(null);
        if (toUpdate != null) {
            toUpdate.update(newData);
            return repository.save(toUpdate);
        }
        return null;
    }

    public VisitRecord delete(int id) {
        VisitRecord visit = repository.findById(id).orElse(null);
        if(visit != null) {
            repository.deleteById(visit.getId());
            return visit;
        } else {
            return null;
        }
    }

    public boolean isTimeAvailable(LocalDateTime start, Duration duration) {
        var end = start.plusMinutes(duration.toMinutes());
        return repository.getRecordsInTime(start, end).size() == 0;
    }
}
