package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.ClientsController;
import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.ClientData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
public class ClientsControllerTest {

    @Mock
    ClientsService clientsService;

    @Test
    public void testGetAllClientsPositive() {
        List<ClientRecord> clients = List.of(
                new ClientRecord(0,  "Emanuel", "Kant"),
                new ClientRecord(1,  "Stephen", "King")
        );
        Mockito.when(clientsService.getAllClients()).thenReturn(clients);
        var expected = ResponseEntity.ok(
                CollectionModel.of(
                        clients.stream()
                        .map(c -> c.add(linkTo(ClientsController.class).slash(c.id).withSelfRel()))
                        .collect(Collectors.toList())
                ).add(linkTo(ClientsController.class).withSelfRel())
        );
        var uut = new ClientsController(clientsService);

        var result = uut.getAllClients();

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).getAllClients();
    }

    @Test
    public void testGetAllClientsEmpty() {
        List<ClientRecord> noClients = Collections.emptyList();
        Mockito.when(clientsService.getAllClients()).thenReturn(noClients);
        var expected = ResponseEntity.ok(
                CollectionModel.empty().add(linkTo(ClientsController.class).withSelfRel())
        );
        var uut = new ClientsController(clientsService);

        var result = uut.getAllClients();

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).getAllClients();
    }

    @Test
    public void testGetClientPositive() {
        int request = 0;
        ClientRecord client = new ClientRecord(request,  "Emanuel", "Kant");
        Mockito.when(clientsService.getClient(Mockito.any(Integer.class))).thenReturn(Optional.of(client));
        var expected = ResponseEntity.ok(
                client.add(linkTo(ClientsController.class).slash(client.id).withSelfRel())
        );
        var uut = new ClientsController(clientsService);

        var result = uut.getClient(request);

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).getClient(request);
    }

    @Test
    public void testGetClientClientNotFound() {
        int request = 0;
        Mockito.when(clientsService.getClient(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        var expected = ResponseToHttp.getFailureResponse(ResponseErrorMessage.CLIENT_NOT_FOUND);
        var uut = new ClientsController(clientsService);

        var result = uut.getClient(request);

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).getClient(request);
    }

    @Test
    public void testAddClientPositive(){
        ClientData requested = new ClientData("Emanuel", "Kant");
        var client = ClientRecord.createClientRecord(requested);
        Mockito.when(clientsService.addClient(Mockito.any(ClientData.class))).thenReturn(Response.succeedResponse(client));
        var expected = ResponseEntity.status(HttpStatus.CREATED).body(
                client.add(linkTo(ClientsController.class).slash(client.id).withSelfRel())
        );
        var uut = new ClientsController(clientsService);

        var result = uut.addClient(requested);

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).addClient(requested);
    }

    @Test
    public void testAddClientMissedData(){
        ClientData requested = new ClientData(null, null);
        Mockito.when(clientsService.addClient(Mockito.any(ClientData.class)))
                .thenReturn(Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ResponseErrorMessage.WRONG_ARGUMENTS.getMessage());
        var uut = new ClientsController(clientsService);

        var result = uut.addClient(requested);

        assert result.equals(expected);
        Mockito.verify(clientsService, Mockito.times(1)).addClient(requested);
    }
}
