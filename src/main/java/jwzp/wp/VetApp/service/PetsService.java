package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.resources.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetsService {

    private final PetsRepository repository;

    @Autowired
    public PetsService(PetsRepository repository) {
        this.repository = repository;
    }

    public List<PetRecord> getAllPets() {
        return repository.findAll();
    }

    public Response<?> addPet(PetData requestedPet) {
        if (!ableToCreateFromData(requestedPet)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
        PetRecord pet = PetRecord.createPetRecord(requestedPet);
        try {
            return Response.succeedResponse(repository.save(pet));
        } catch (IllegalArgumentException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(PetData pet) {
        return pet.name != null && pet.owner != null && pet.birthday != null && pet.animal != null;
    }
}
