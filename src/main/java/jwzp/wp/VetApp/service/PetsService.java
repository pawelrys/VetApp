package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PetsService {

    private final PetsRepository petsRepository;
    private final ClientsRepository ownersRepository;

    @Autowired
    public PetsService(PetsRepository petsRepository, ClientsRepository ownersRepository) {
        this.petsRepository = petsRepository;
        this.ownersRepository = ownersRepository;
    }

    public List<PetRecord> getAllPets() {
        return petsRepository.findAll();
    }

    public Optional<PetRecord> getPet(int id) {
        return petsRepository.findById(id);
    }

    public Response<PetRecord> addPet(PetData requestedPet) {
        if (!ableToCreateFromData(requestedPet)) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }

        try {
            ClientRecord owner = ownersRepository.findById(requestedPet.ownerId).orElseThrow();
            PetRecord pet = PetRecord.createPetRecord(
                    requestedPet.name,
                    requestedPet.birthday,
                    requestedPet.animal,
                    owner
            );
            return Response.succeedResponse(petsRepository.save(pet));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(PetData pet) {
        return pet.name != null && pet.ownerId != null && pet.birthday != null && pet.animal != null;
    }
}
