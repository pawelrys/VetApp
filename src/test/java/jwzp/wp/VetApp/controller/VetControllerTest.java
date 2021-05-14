package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.VetsController;
import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import jwzp.wp.VetApp.service.VetsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
public class VetControllerTest {

    @Mock
    VetsService vetsService;

    @Test
    public void testGetAllVetsPositive() throws Exception {
        List<VetRecord> vets = List.of(
                new VetRecord(0,  "Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00")),
                new VetRecord(1,  "Piotr", "Kruk", new byte[0], LocalTime.parse("07:00:00"), LocalTime.parse("16:00:00"))
        );
        Mockito.when(vetsService.getAllVets()).thenReturn(vets);
        var expected = ResponseEntity.ok(
                CollectionModel.of(
                        vets.stream()
                                .map(c -> c.add(linkTo(VetsController.class).slash(c.id).withSelfRel()))
                                .collect(Collectors.toList())
                ).add(linkTo(VetsController.class).withSelfRel())
        );
        var uut = new VetsController(vetsService);

        var result = uut.getAllVets();

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).getAllVets();
    }

    @Test
    public void testGetAllVetsEmpty() throws Exception {
        List<VetRecord> noVets = Collections.emptyList();
        Mockito.when(vetsService.getAllVets()).thenReturn(noVets);
        var expected = ResponseEntity.ok(
                CollectionModel.empty().add(linkTo(VetsController.class).withSelfRel())
        );
        var uut = new VetsController(vetsService);

        var result = uut.getAllVets();

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).getAllVets();
    }

    @Test
    public void testGetVetPositive() throws Exception {
        int request = 0;
        VetRecord Vet = new VetRecord(request,  "Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        Mockito.when(vetsService.getVet(request)).thenReturn(Optional.of(Vet));
        var expected = ResponseEntity.ok(
                Vet.add(linkTo(VetsController.class).slash(Vet.id).withSelfRel())
        );
        var uut = new VetsController(vetsService);

        var result = uut.getVet(request);

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).getVet(request);
    }

    @Test
    public void testGetVetVetNotFound() throws Exception {
        int request = 0;
        VetRecord Vet = new VetRecord(1, "Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        Mockito.when(vetsService.getVet(request)).thenReturn(Optional.empty());
        var expected = ResponseToHttp.getFailureResponse(ResponseErrorMessage.VET_NOT_FOUND);
        var uut = new VetsController(vetsService);

        var result = uut.getVet(request);

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).getVet(request);
    }

    @Test
    public void testAddVetPositive(){
        VetData requested = new VetData("Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        var Vet = VetRecord.createVetRecord(requested);
        Mockito.when(vetsService.addVet(requested)).thenReturn(Response.succeedResponse(Vet));
        var expected = ResponseEntity.status(HttpStatus.CREATED).body(
                Vet.add(linkTo(VetsController.class).slash(Vet.id).withSelfRel())
        );
        var uut = new VetsController(vetsService);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).addVet(requested);
    }

    @Test
    public void testAddVetMissedData(){
        VetData requested = new VetData(null, null, null, null, null);
        Mockito.when(vetsService.addVet(requested))
                .thenReturn(Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ResponseErrorMessage.WRONG_ARGUMENTS.getMessage());
        var uut = new VetsController(vetsService);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsService, Mockito.times(1)).addVet(requested);
    }
}
