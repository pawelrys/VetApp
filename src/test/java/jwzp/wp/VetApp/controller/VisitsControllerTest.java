package jwzp.wp.VetApp.controller;

import jwzp.wp.VetApp.controller.api.*;
import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.*;
import jwzp.wp.VetApp.models.utils.VetsTimeInterval;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.VisitsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
public class VisitsControllerTest {

    private ClientRecord owner;
    private PetRecord puszek;
    private PetRecord kebsik;
    private VetRecord vet;
    private OfficeRecord office;

    @Mock
    VisitsService visitsService;

    @BeforeEach
    private void setup(){
        owner = new ClientRecord(0, "Gavin", "Hesketh");
        puszek = new PetRecord(1, "Puszek", LocalDate.parse("2020-03-05"), Animal.Cat, owner);
        kebsik = new PetRecord(2, "Kebsik", LocalDate.parse("2019-12-10"), Animal.Dog, owner);
        vet = new VetRecord(3, "Gregory", "House", new byte[0], LocalTime.parse("07:00:00"), LocalTime.parse("22:00:00"));
        office = new OfficeRecord(4, "Princeton-Plainsboro");
    }

    @Test
    public void testGetAllVisitsPositive() {
        List<VisitRecord> visits = List.of(
                new VisitRecord(
                        5,
                        LocalDateTime.parse("2021-02-21T12:30:00"),
                        Duration.of(20, ChronoUnit.MINUTES),
                        puszek,
                        Status.PENDING,
                        BigDecimal.valueOf(80),
                        office,
                        vet
                ),
                new VisitRecord(
                        6,
                        LocalDateTime.parse("2021-02-21T12:55:00"),
                        Duration.of(45, ChronoUnit.MINUTES),
                        kebsik,
                        Status.PENDING,
                        BigDecimal.valueOf(120),
                        office,
                        vet
                )
        );
        Mockito.when(visitsService.getAllVisits()).thenReturn(visits);
        var expected = ResponseEntity.ok(
                CollectionModel.of(
                        visits.stream()
                                .map(this::addLinksToVisitComponents)
                                .collect(Collectors.toList())
                ).add(linkTo(VisitsController.class).withSelfRel())
        );
        var uut = new VisitsController(visitsService);

        var result = uut.getAllVisits();

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).getAllVisits();
    }

    @Test
    public void testGetAllVisitsEmpty() {
        List<VisitRecord> visits = Collections.emptyList();
        Mockito.when(visitsService.getAllVisits()).thenReturn(visits);
        var expected = ResponseEntity.ok(
                CollectionModel.empty(linkTo(VisitsController.class).withSelfRel())
        );
        var uut = new VisitsController(visitsService);

        var result = uut.getAllVisits();

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).getAllVisits();
    }

    @Test
    public void testGetVisitPositive() {
        var visit = new VisitRecord(
                5,
                LocalDateTime.parse("2021-04-08T08:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                puszek,
                Status.PENDING,
                BigDecimal.valueOf(20),
                office,
                vet
        );
        int request = visit.getId();
        Mockito.when(visitsService.getVisit(Mockito.any(Integer.class))).thenReturn(Optional.of(visit));
        var expected = ResponseEntity.ok(addLinksToVisitComponents(visit));
        var uut = new VisitsController(visitsService);

        var result = uut.getVisit(request);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).getVisit(request);
    }

    @Test
    public void testGetVisitNotFound() {
        int request = 0;
        Mockito.when(visitsService.getVisit(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        var expected = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND).getMessage());
        var uut = new VisitsController(visitsService);

        var result = uut.getVisit(request);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).getVisit(request);
    }

    @Test
    public void testUpdateVisitPositive(){
        int requestedId = 0;
        VisitData requestedData = new VisitData(
                LocalDateTime.parse("2021-04-08T08:00:00"),
                Duration.of(1, ChronoUnit.HOURS),
                null,
                null,
                BigDecimal.valueOf(80),
                null,
                null
        );
        var visitAfterUpdate = new VisitRecord(
                requestedId,
                requestedData.startDate,
                requestedData.duration,
                puszek,
                Status.PENDING,
                requestedData.price,
                office,
                vet
        );

        Mockito.when(visitsService.updateVisit(Mockito.any(Integer.class), Mockito.any(VisitData.class)))
                .thenReturn(Response.succeedResponse(visitAfterUpdate));
        var expected = ResponseEntity.ok(addLinksToVisitComponents(visitAfterUpdate));
        var uut = new VisitsController(visitsService);

        var result = uut.updateVisit(requestedId, requestedData);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).updateVisit(requestedId, requestedData);
    }


    @Test
    public void testUpdateVisitNotFound(){
        int requestedId = 0;
        VisitData requestedData = new VisitData(
                LocalDateTime.parse("2021-04-08T08:00:00"),
                Duration.of(1, ChronoUnit.HOURS),
                null,
                null,
                BigDecimal.valueOf(80),
                null,
                null
        );

        Mockito.when(visitsService.updateVisit(Mockito.any(Integer.class), Mockito.any(VisitData.class)))
                .thenReturn(Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND)));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND).getMessage());
        var uut = new VisitsController(visitsService);

        var result = uut.updateVisit(requestedId, requestedData);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).updateVisit(requestedId, requestedData);
    }

    @Test
    public void testAddVisitPositive(){
        VisitData requested = new VisitData(
                LocalDateTime.parse("2021-04-08T08:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                puszek.id,
                null,
                BigDecimal.valueOf(20),
                office.id,
                vet.id
        );
        var visit = VisitRecord.createVisitRecord(
                requested.startDate,
                requested.duration,
                puszek,
                requested.price,
                office,
                vet
        );
        Mockito.when(visitsService.addVisit(Mockito.any(VisitData.class))).thenReturn(Response.succeedResponse(visit));
        var expected = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addLinksToVisitComponents(visit));
        var uut = new VisitsController(visitsService);

        var result = uut.addVisit(requested);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).addVisit(requested);
    }

    @Test
    public void testAddClientMissedData(){
        VisitData requested = new VisitData(
                LocalDateTime.parse("2021-04-08T08:30:00"),
                null,
                puszek.id,
                null,
                BigDecimal.valueOf(20),
                office.id,
                vet.id
        );
        Mockito.when(visitsService.addVisit(Mockito.any(VisitData.class)))
                .thenReturn(Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS)));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS).getMessage());
        var uut = new VisitsController(visitsService);

        var result = uut.addVisit(requested);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).addVisit(requested);
    }

    @Test
    public void testDeleteVisitPositive(){
        var visit = new VisitRecord(
                1,
                LocalDateTime.parse("2021-04-08T08:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                puszek,
                Status.PENDING,
                BigDecimal.valueOf(20),
                office,
                vet
        );
        int requested = visit.getId();
        Mockito.when(visitsService.delete(Mockito.any(Integer.class))).thenReturn(Response.succeedResponse(visit));
        var expected = ResponseEntity.ok(addLinksToVisitComponents(visit));
        var uut = new VisitsController(visitsService);

        var result = uut.deleteVisit(requested);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).delete(requested);
    }

    @Test
    public void testDeleteVisitNotFound(){
        int requested = 1;
        Mockito.when(visitsService.delete(Mockito.any(Integer.class)))
                .thenReturn(Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND)));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND).getMessage());
        var uut = new VisitsController(visitsService);

        var result = uut.deleteVisit(requested);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1)).delete(requested);
    }

    @Test
    public void testGetAvailableTimeSlotsPositive() {
        LocalDateTime requestedBeg = LocalDateTime.parse("2021-08-12T11:30:00");
        LocalDateTime requestedEnd = LocalDateTime.parse("2021-08-12T12:15:00");
        List<Integer> requestedVetIds = List.of(2, 3);

        var intervals = List.of(
                new VetsTimeInterval(
                        LocalDateTime.parse("2021-08-12T11:30:00"),
                        LocalDateTime.parse("2021-08-12T11:45:00"),
                        List.of(2, 3)),
                new VetsTimeInterval(
                        LocalDateTime.parse("2021-08-12T11:45:00"),
                        LocalDateTime.parse("2021-08-12T12:00:00"),
                        List.of(2)),
                new VetsTimeInterval(
                        LocalDateTime.parse("2021-08-12T12:00:00"),
                        LocalDateTime.parse("2021-08-12T12:15:00"),
                        List.of(2))
        );

        Mockito.when(visitsService.availableTimeSlots(
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(List.class)))
                .thenReturn(Response.succeedResponse(intervals));
        var expected = ResponseEntity.ok(intervals);
        var uut = new VisitsController(visitsService);

        var result = uut.getAvailableTimeSlots(requestedBeg, requestedEnd, requestedVetIds);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1))
                .availableTimeSlots(requestedBeg, requestedEnd, requestedVetIds);
    }

    @Test
    public void testGetAvailableTimeSlotsNoAvailable() {
        LocalDateTime requestedBeg = LocalDateTime.parse("2021-08-12T11:30:00");
        LocalDateTime requestedEnd = LocalDateTime.parse("2021-08-12T12:15:00");
        List<Integer> requestedVetIds = List.of(2, 3);

        List<VetsTimeInterval> intervals = Collections.emptyList();
        Mockito.when(visitsService.availableTimeSlots(
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(List.class)))
                .thenReturn(Response.succeedResponse(intervals));
        var expected = ResponseEntity.ok(intervals);
        var uut = new VisitsController(visitsService);

        var result = uut.getAvailableTimeSlots(requestedBeg, requestedEnd, requestedVetIds);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1))
                .availableTimeSlots(requestedBeg, requestedEnd, requestedVetIds);
    }

    @Test
    public void testGetAvailableTimeSlotsMissingData() {
        LocalDateTime requestedBeg = LocalDateTime.parse("2021-08-12T11:30:00");
        List<Integer> requestedVetIds = List.of(2, 3);

        Mockito.when(visitsService.availableTimeSlots(
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS)));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS).getMessage()
        );
        var uut = new VisitsController(visitsService);

        var result = uut.getAvailableTimeSlots(requestedBeg, null, requestedVetIds);

        assert result.equals(expected);
        Mockito.verify(visitsService, Mockito.times(1))
                .availableTimeSlots(requestedBeg, null, requestedVetIds);
    }

    private VisitRecord addLinksToVisitComponents(VisitRecord v){
        v.add(linkTo(VisitsController.class).slash(v.getId()).withSelfRel());
        v.add(linkTo(PetsController.class).slash(v.pet.id).withRel("pet"));
        v.add(linkTo(ClientsController.class).slash(v.pet.owner.id).withRel("client"));
        v.add(linkTo(VetsController.class).slash(v.vet.id).withRel("vet"));
        v.add(linkTo(OfficesController.class).slash(v.office.id).withRel("office"));
        return v;
    }
}
