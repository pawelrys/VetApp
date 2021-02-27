package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.VisitRecord;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
