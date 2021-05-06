package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PetsService {

    public final Logger logger = LogManager.getLogger(PetsService.class);
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
            logger.info(LogsUtils.logMissingData(requestedPet));
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
            var savedPet = petsRepository.save(pet);
            logger.info(LogsUtils.logSaved(savedPet, savedPet.id));
            return Response.succeedResponse(savedPet);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.error(e);
            return Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        }
    }

    public boolean ableToCreateFromData(PetData pet) {
        return pet.name != null && pet.ownerId != null && pet.birthday != null && pet.animal != null;
    }
}
