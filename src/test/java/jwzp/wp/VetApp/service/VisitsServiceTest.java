package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.*;
import jwzp.wp.VetApp.models.utils.VetsTimeInterval;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.resources.*;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class VisitsServiceTest {

    @Mock
    VisitsRepository visitsRepository;

    @Mock
    PetsRepository petsRepository;

    @Mock
    OfficesRepository officesRepository;

    @Mock
    VetsRepository vetsRepository;

    private ClientRecord owner;
    private PetRecord pet;
    private VetRecord vet;
    private OfficeRecord office;
    private Clock clock;

    @BeforeEach
    private void setup() {
        owner = new ClientRecord(0, "Gavin", "Hesketh");
        pet = new PetRecord(1, "Puszek", LocalDate.parse("2020-03-05"), Animal.Cat, owner);
        vet = new VetRecord(3, "Gregory", "House", new byte[0],
                LocalTime.parse("07:00:00"), LocalTime.parse("22:00:00"));
        office = new OfficeRecord(4, "Princeton-Plainsboro");
        clock = Clock.fixed(Instant.parse("2021-04-21T14:30:52.984310Z"), ZoneId.of("Europe/Warsaw"));
    }

    @Test
    public void TestAddVisitPositive() {
        var requested = new VisitData(
                LocalDateTime.parse("2021-04-22T08:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                pet.id,
                null,
                BigDecimal.valueOf(20),
                office.id,
                vet.id
        );
        var visit = VisitRecord.createVisitRecord(
                requested.startDate,
                requested.duration,
                pet,
                requested.price,
                office,
                vet
        );
        Mockito.when(visitsRepository.save(Mockito.any(VisitRecord.class))).thenReturn(visit);
        Mockito.when(petsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(pet));
        Mockito.when(officesRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(office));
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        var expected = Response.succeedResponse(visit);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.addVisit(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(1)).save(visit);
    }

    @Test
    public void TestAddVisitMissingData() {
        var requested = new VisitData(
                LocalDateTime.parse("2021-04-22T08:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                pet.id,
                null,
                null,
                office.id,
                vet.id
        );
        var expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.addVisit(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(0)).save(Mockito.any(VisitRecord.class));
        Mockito.verify(petsRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(officesRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(vetsRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
    }

    @Test
    public void TestAddVisitToLateToBook() {
        var requested = new VisitData(
                LocalDateTime.parse("2021-04-21T15:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                pet.id,
                null,
                BigDecimal.valueOf(120),
                office.id,
                vet.id
        );
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        var expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_TIME_UNAVAILABLE));
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.addVisit(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(0)).save(Mockito.any(VisitRecord.class));
        Mockito.verify(petsRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(officesRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(vetsRepository, Mockito.times(1)).findById(vet.id);
    }

    @Test
    public void TestAddVisitWrongPetId() {
        var requested = new VisitData(
                LocalDateTime.parse("2021-04-23T15:30:00"),
                Duration.of(5, ChronoUnit.MINUTES),
                pet.id,
                null,
                BigDecimal.valueOf(120),
                office.id,
                vet.id
        );
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        Mockito.when(petsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        var expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.addVisit(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(0)).save(Mockito.any(VisitRecord.class));
        Mockito.verify(petsRepository, Mockito.times(1)).findById(pet.id);
        Mockito.verify(officesRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(vetsRepository, Mockito.times(1)).findById(vet.id);
    }

    @Test
    public void testUpdateVisitPositive() {
        var requestedData = new VisitData(
                null,
                Duration.of(5, ChronoUnit.MINUTES),
                pet.id,
                null,
                BigDecimal.valueOf(120),
                office.id,
                vet.id
        );
        int requestedId = 5;
        var visitAfterUpdate = new VisitRecord(
                requestedId,
                LocalDateTime.parse("2021-05-10T08:30:00"),
                requestedData.duration,
                pet,
                Status.PENDING,
                requestedData.price,
                office,
                vet
        );
        Mockito.when(visitsRepository.save(Mockito.any(VisitRecord.class))).thenReturn(visitAfterUpdate);
        Mockito.when(visitsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(visitAfterUpdate));
        Mockito.when(visitsRepository.getRegisteredVisitsInTime(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Integer.class),
                Mockito.any(Integer.class)))
                .thenReturn(Collections.emptyList());
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        Mockito.when(officesRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(office));
        Mockito.when(petsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(pet));
        var expected = Response.succeedResponse(visitAfterUpdate);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.updateVisit(requestedId, requestedData);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(1)).save(visitAfterUpdate);
        Mockito.verify(visitsRepository, Mockito.times(1)).findById(requestedId);
        Mockito.verify(visitsRepository, Mockito.times(1)).getRegisteredVisitsInTime(
                visitAfterUpdate.startDate,
                visitAfterUpdate.startDate.plus(visitAfterUpdate.duration),
                visitAfterUpdate.office.id,
                visitAfterUpdate.vet.id
        );
        Mockito.verify(vetsRepository, Mockito.times(2)).findById(vet.id);
    }

    @Test
    public void testUpdateVisitBusyVet() {
        var requestedData = new VisitData(
                LocalDateTime.parse("2021-05-10T12:30:00"),
                Duration.of(30, ChronoUnit.MINUTES),
                pet.id,
                null,
                BigDecimal.valueOf(120),
                office.id,
                vet.id
        );
        int requestedId = 5;
        var visitAfterUpdate = new VisitRecord(
                requestedId,
                LocalDateTime.parse("2021-05-10T08:30:00"),
                requestedData.duration,
                pet,
                Status.PENDING,
                requestedData.price,
                office,
                vet
        );
        var collidingVisit = new VisitRecord(
                6,
                LocalDateTime.parse("2021-05-10T12:40:00"),
                Duration.of(1, ChronoUnit.HOURS),
                pet,
                Status.PENDING,
                requestedData.price,
                office,
                vet
        );
        Mockito.when(visitsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(visitAfterUpdate));
        Mockito.when(visitsRepository.getRegisteredVisitsInTime(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Integer.class),
                Mockito.any(Integer.class)))
                .thenReturn(List.of(collidingVisit));
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        Mockito.when(officesRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(office));
        Mockito.when(petsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(pet));
        var expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.BUSY_VET));
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.updateVisit(requestedId, requestedData);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(0)).save(Mockito.any(VisitRecord.class));
        Mockito.verify(visitsRepository, Mockito.times(1)).findById(requestedId);
        Mockito.verify(visitsRepository, Mockito.times(1)).getRegisteredVisitsInTime(
                requestedData.startDate,
                requestedData.startDate.plus(requestedData.duration),
                requestedData.officeId,
                requestedData.vetId
        );
        Mockito.verify(vetsRepository, Mockito.times(2)).findById(vet.id);
    }

    @Test
    public void TestDeleteVisitPositive() {
        int requested = 5;
        var visit = new VisitRecord(
                requested,
                LocalDateTime.parse("2022-04-26T10:57:00"),
                Duration.of(2, ChronoUnit.HOURS),
                pet,
                Status.PENDING,
                BigDecimal.valueOf(300),
                office,
                vet
        );
        Mockito.when(visitsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(visit));
        var expected = Response.succeedResponse(visit);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.delete(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(1)).findById(requested);
        Mockito.verify(visitsRepository, Mockito.times(1)).deleteById(requested);
    }

    @Test
    public void TestDeleteVisitNotFound() {
        int requested = 5;
        var visit = new VisitRecord(
                6,
                LocalDateTime.parse("2022-04-26T10:57:00"),
                Duration.of(2, ChronoUnit.HOURS),
                pet,
                Status.PENDING,
                BigDecimal.valueOf(300),
                office,
                vet
        );
        Mockito.when(visitsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        var expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.VISIT_NOT_FOUND));
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.delete(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(1)).findById(requested);
        Mockito.verify(visitsRepository, Mockito.times(0)).deleteById(requested);
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/ableToCreateFromDataTestVisits.csv", numLinesToSkip = 1)
    public void testAbleToCreateFromData(
            String testCaseName,
            LocalDateTime startDate,
            Duration duration,
            Integer petId,
            BigDecimal price,
            Integer officeId,
            Integer vetId,
            boolean result
    ) {
        var requested = new VisitData(startDate, duration, petId, Status.PENDING, price, officeId, vetId);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        assert uut.ableToCreateFromData(requested) == result;
    }

    @Test
    public void testAvailableTimeSlots() {
        LocalDateTime requestedBeg = LocalDateTime.parse("2022-04-26T09:00:00");
        LocalDateTime requestedEnd = LocalDateTime.parse("2022-04-26T10:00:00");
        List<Integer> requestedVetIds = List.of(1, 3);

        List<VetsTimeInterval> freeSlots = List.of(
                new VetsTimeInterval (
                        LocalDateTime.parse("2022-04-26T09:00:00"),
                        LocalDateTime.parse("2022-04-26T09:15:00"),
                        Collections.singletonList(3)
                ),
                new VetsTimeInterval (
                        LocalDateTime.parse("2022-04-26T09:15:00"),
                        LocalDateTime.parse("2022-04-26T09:30:00"),
                        Collections.singletonList(1)
                ),
                new VetsTimeInterval (
                        LocalDateTime.parse("2022-04-26T09:45:00"),
                        LocalDateTime.parse("2022-04-26T10:00:00"),
                        Collections.singletonList(1)
                ),
                new VetsTimeInterval (
                        LocalDateTime.parse("2022-04-26T09:45:00"),
                        LocalDateTime.parse("2022-04-26T10:00:00"),
                        Collections.singletonList(3)
                )
        );
        var expected = Response.succeedResponse(List.of(
                new VetsTimeInterval(
                        LocalDateTime.parse("2022-04-26T09:00:00"),
                        LocalDateTime.parse("2022-04-26T09:15:00"),
                        List.of(3)
                ),
                new VetsTimeInterval(
                        LocalDateTime.parse("2022-04-26T09:15:00"),
                        LocalDateTime.parse("2022-04-26T09:30:00"),
                        List.of(1)
                ),
                new VetsTimeInterval(
                        LocalDateTime.parse("2022-04-26T09:45:00"),
                        LocalDateTime.parse("2022-04-26T10:00:00"),
                        List.of(1, 3)
                )
        ));
        Mockito.when(visitsRepository.getAvailableTimeSlots(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(freeSlots);

        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.availableTimeSlots(requestedBeg, requestedEnd, requestedVetIds);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(1))
                .getAvailableTimeSlots(requestedBeg, requestedEnd);
    }

    @Test
    public void testAutomaticallyClosePastVisits(){
        List<VisitRecord> visits = Collections.emptyList();
        Mockito.when(visitsRepository.getPastVisitsWithStatus(Mockito.any(LocalDateTime.class), Mockito.any(Status.class))).thenReturn(visits);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        uut.automaticallyClosePastVisits();

        Mockito.verify(visitsRepository, Mockito.times(1))
                .getPastVisitsWithStatus(LocalDateTime.now(clock), Status.PENDING);
    }
}