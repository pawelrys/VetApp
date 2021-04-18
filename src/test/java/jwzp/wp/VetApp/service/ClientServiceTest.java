package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.resources.ClientsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
