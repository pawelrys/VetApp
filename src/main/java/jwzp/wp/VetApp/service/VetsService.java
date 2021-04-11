package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VetsService {

    private final VetsRepository repository;

    @Autowired
    public VetsService(VetsRepository repository) {
        this.repository = repository;
    }

    public List<VetRecord> getAllVets() {
        return repository.findAll();
    }

    public Optional<VetRecord> getVet(int id){
        return repository.findById(id);
    }

    public Response<VetRecord> addVet(VetData requestedVet) {
        if (!ableToCreateFromData(requestedVet)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        VetRecord vet = VetRecord.createVet(requestedVet);
        try {
            return Response.succeedResponse(repository.save(vet));
        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(VetData vet) {
        return vet.name != null && vet.surname != null && vet.officeHoursEnd != null && vet.officeHoursStart != null && vet.photo != null;
    }
}
