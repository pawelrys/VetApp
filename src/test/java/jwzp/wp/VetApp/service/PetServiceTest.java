package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.resources.PetsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    PetsRepository petsRepository;
    @Mock
    ClientsRepository clientsRepository;

    @Test
    public void testAddPetPositive() throws Exception {
        PetData requested = new PetData("Reksio", LocalDate.parse("2020-01-01"), Animal.Dog, 1);
        ClientRecord client = new ClientRecord(1, "Jan", "Kowalski");
        Response<PetRecord> expected = Response.succeedResponse(new PetRecord(1, "Reksio", LocalDate.parse("2020-01-01"), Animal.Dog, client));
        Mockito.when(petsRepository.save(Mockito.any(PetRecord.class))).thenReturn(expected.get());
        Mockito.when(clientsRepository.findById(Mockito.any(int.class))).thenReturn(java.util.Optional.of(client));
        var uut = new PetsService(petsRepository, clientsRepository);

        var result = uut.addPet(requested);

        assert expected.equals(result);
        Mockito.verify(petsRepository, Mockito.times(1)).save(PetRecord.createPetRecord(requested.name, requested.birthday, requested.animal, client));
    }

    @Test
    public void testAddPetMissingData() throws Exception {
        PetData requested = new PetData(null, null, null, null);
        Response<?> expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        var uut = new PetsService(petsRepository, clientsRepository);

        var result = uut.addPet(requested);

        assert result.equals(expected);
        Mockito.verify(clientsRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testAddPetRepositoryException() throws Exception {
        PetData requested = new PetData("Reksio", LocalDate.parse("2020-01-01"), Animal.Dog, 1);
        ClientRecord client = new ClientRecord(1, "Jan", "Kowalski");
        Response<PetRecord> expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        Mockito.when(petsRepository.save(Mockito.any(PetRecord.class))).thenThrow(new IllegalArgumentException());
        Mockito.when(clientsRepository.findById(Mockito.any(int.class))).thenReturn(java.util.Optional.of(client));
        var uut = new PetsService(petsRepository, clientsRepository);

        var result = uut.addPet(requested);

        assert result.equals(expected);
        Mockito.verify(petsRepository, Mockito.times(1)).save(PetRecord.createPetRecord(requested.name, requested.birthday, requested.animal, client));
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/ableToCreateFromDataTestPet.csv", numLinesToSkip = 1)
    public void testAbleToCreateFromData(String testCaseName, String name, LocalDate birthday, Animal animal, Integer ownerId, Boolean result) throws Exception {
        var requested = new PetData(name, birthday, animal, ownerId);
        var uut = new PetsService(petsRepository, clientsRepository);

        assert uut.ableToCreateFromData(requested) == result;
    }
}
