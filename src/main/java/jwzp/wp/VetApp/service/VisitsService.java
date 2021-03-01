package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitData;
import jwzp.wp.VetApp.models.Animal;
import jwzp.wp.VetApp.models.Status;
import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
        VisitRecord visit = new VisitRecord(requestedVisit);
        try {
            return repository.save(visit);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean updateVisit(int id, Map<String, Object> updatedFields){
        VisitRecord toUpdate = repository.findById(id).orElse(null);
        if (toUpdate == null) {
            return false;
        }

        if (updatedFields.containsKey("startDate")){
            toUpdate.startDate = (LocalDate) updatedFields.get("startDate");
        }
        if (updatedFields.containsKey("duration")){
            toUpdate.duration = (Duration) updatedFields.get("duration");
        }
        if (updatedFields.containsKey("animalKind")){
            toUpdate.animalKind = (Animal) updatedFields.get("animalKind");
        }
        if (updatedFields.containsKey("status")){
            toUpdate.status = (Status) updatedFields.get("status");
        }
        return true;
    }

    public boolean isTimeAvailable(LocalDate start, Duration duration) {
        return true;
    }

}
