package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.LogsUtils;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
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
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
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
            logger.info(LogsUtils.logException(e));
            return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        }
    }

    public Response<PetRecord> updatePet(int id, PetData newData) {
        Optional<PetRecord> toUpdate = petsRepository.findById(id);

        if (toUpdate.isPresent()) {
            toUpdate.get().update(newData);
            var saved = petsRepository.save(toUpdate.get());
            logger.info(LogsUtils.logUpdated(saved, saved.id));
            return Response.succeedResponse(toUpdate.get());
        }
        logger.info(LogsUtils.logNotFoundObject(PetRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.PET_NOT_FOUND));
    }

    public Response<PetRecord> delete(int id) {
        Optional<PetRecord> petOpt = petsRepository.findById(id);
        if (petOpt.isPresent()) {
            PetRecord pet = petOpt.get();
            petsRepository.deleteById(pet.id);
            logger.info(LogsUtils.logDeleted(pet, pet.id));
            return Response.succeedResponse(pet);
        }
        logger.info(LogsUtils.logNotFoundObject(PetRecord.class, id));
        return Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.PET_NOT_FOUND));
    }

    public boolean ableToCreateFromData(PetData pet) {
        return pet.name != null && pet.ownerId != null && pet.birthday != null && pet.animal != null;
    }
}
