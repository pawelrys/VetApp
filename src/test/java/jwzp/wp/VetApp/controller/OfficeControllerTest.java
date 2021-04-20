package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.OfficesController;
import jwzp.wp.VetApp.controller.api.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.service.OfficesService;
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
public class OfficeControllerTest {

    @Mock
    OfficesService officesService;

    @Test
    public void testGetAllOfficesPositive() throws Exception {
        List<OfficeRecord> offices = List.of(
                new OfficeRecord(0, "Pinokio"),
                new OfficeRecord(1, "Kret")
        );
        Mockito.when(officesService.getAllOffices()).thenReturn(offices);
        var expected = ResponseEntity.ok(
                CollectionModel.of(
                        offices.stream()
                                .map(c -> c.add(linkTo(OfficesController.class).slash(c.id).withSelfRel()))
                                .collect(Collectors.toList())
                ).add(linkTo(OfficesController.class).withSelfRel())
            );
        var uut = new OfficesController(officesService);

        var result = uut.getAllOffices();

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).getAllOffices();
    }

    @Test
    public void testGetAllOfficesEmpty() throws Exception {
        List<OfficeRecord> noOffices = Collections.emptyList();
        Mockito.when(officesService.getAllOffices()).thenReturn(noOffices);
        var expected = ResponseEntity.ok(
                CollectionModel.empty().add(linkTo(OfficesController.class).withSelfRel())
        );
        var uut = new OfficesController(officesService);

        var result = uut.getAllOffices();

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).getAllOffices();
    }

    @Test
    public void testGetOfficePositive() throws Exception {
        int request = 0;
        OfficeRecord office = new OfficeRecord(request, "Pinokio");
        Mockito.when(officesService.getOffice(request)).thenReturn(Optional.of(office));
        var expected = ResponseEntity.ok(
                office.add(linkTo(OfficesController.class).slash(office.id).withSelfRel())
        );
        var uut = new OfficesController(officesService);

        var result = uut.getOffice(request);

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).getOffice(request);
    }

    @Test
    public void testGetOfficeOfficeNotFound() throws Exception {
        int request = 0;
        Mockito.when(officesService.getOffice(request)).thenReturn(Optional.empty());
        var expected = ResponseToHttp.getFailureResponse(ResponseErrorMessage.OFFICE_NOT_FOUND);
        var uut = new OfficesController(officesService);

        var result = uut.getOffice(request);

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).getOffice(request);
    }

    @Test
    public void testAddOfficePositive(){
        OfficeData requested = new OfficeData("Pinokio");
        var Office = OfficeRecord.createOfficeRecord(requested);
        Mockito.when(officesService.addOffice(requested)).thenReturn(Response.succeedResponse(Office));
        var expected = ResponseEntity.status(HttpStatus.CREATED).body(
                Office.add(linkTo(OfficesController.class).slash(Office.id).withSelfRel())
        );
        var uut = new OfficesController(officesService);

        var result = uut.addOffice(requested);

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).addOffice(requested);
    }

    @Test
    public void testAddOfficeMissedData(){
        OfficeData requested = new OfficeData(null);
        Mockito.when(officesService.addOffice(requested))
                .thenReturn(Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ResponseErrorMessage.WRONG_ARGUMENTS.getMessage());
        var uut = new OfficesController(officesService);

        var result = uut.addOffice(requested);

        assert result.equals(expected);
        Mockito.verify(officesService, Mockito.times(1)).addOffice(requested);
    }
}

