package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ClientsServiceTest {

    @Mock
    ClientsRepository clientsRepository;

    @Test
    public void testAddClientPositive() {
        ClientData requested = new ClientData("Marcus", "Aurelius");
        Response<ClientRecord> expected = Response.succeedResponse(new ClientRecord(1, "Marcus", "Aurelius"));
        Mockito.when(clientsRepository.save(Mockito.any(ClientRecord.class))).thenReturn(expected.get());
        var uut = new ClientsService(clientsRepository);

        var result = uut.addClient(requested);

        assert expected.equals(result);
        Mockito.verify(clientsRepository, Mockito.times(1)).save(ClientRecord.createClientRecord(requested.name, requested.surname));
    }

    @Test
    public void testAddClientMissingData() {
        ClientData requested = new ClientData(null, null);
        var errorBuilder = new ErrorMessagesBuilder();
        errorBuilder.addToMessage("name");
        errorBuilder.addToMessage("surname");
        var error = errorBuilder.build("Missing fields: ");
        var expected = Response.errorResponse(error);        var uut = new ClientsService(clientsRepository);

        var result = uut.addClient(requested);


        assert result.equals(expected);
        Mockito.verify(clientsRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testAddClientRepositoryException() {
        ClientData requested = new ClientData("Marcus", "Aurelius");
        Response<ClientRecord> expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        Mockito.when(clientsRepository.save(Mockito.any(ClientRecord.class))).thenThrow(new IllegalArgumentException());
        var uut = new ClientsService(clientsRepository);

        var result = uut.addClient(requested);

        assert result.equals(expected);
        Mockito.verify(clientsRepository, Mockito.times(1)).save(ClientRecord.createClientRecord(requested.name, requested.surname));
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/ableToCreateFromDataTestInput.csv", numLinesToSkip = 1)
    public void testAbleToCreateFromData(String testCaseName, String name, String surname, boolean result) {
        var requested = new ClientData(name, surname);
        var uut = new ClientsService(clientsRepository);

        assert uut.ableToCreateFromData(requested) == result;
    }
}
