package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    ClientsRepository clientsRepository;

    @Test
    public void testAddClient() throws Exception {
        ClientData requested = new ClientData("Marcus", "Aurelius");
        Response<ClientRecord> expected = Response.succeedResponse(new ClientRecord(1, "Marcus", "Aurelius"));
        Mockito.when(clientsRepository.save(Mockito.any())).thenReturn(expected.get());

        var uut = new ClientsService(clientsRepository);
        var result = uut.addClient(requested);

        assert result.equals(expected);
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp.service/ableToCreateFromDataTestInput.csv", numLinesToSkip = 1)
    public void testAbleToCreateFromData(String testCaseName, String name, String surname, boolean result) throws Exception {
        var requested = new ClientData(name, surname);

        var uut = new ClientsService(clientsRepository);

        assert uut.ableToCreateFromData(requested) == result;
    }
}
