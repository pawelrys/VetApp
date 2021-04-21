package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.records.*;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.resources.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
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
    private void setup(){
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
        var visit = VisitRecord.createNewVisit(
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
        var visit = VisitRecord.createNewVisit(
                requested.startDate,
                requested.duration,
                pet,
                requested.price,
                office,
                vet
        );
        var expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
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
        var visit = VisitRecord.createNewVisit(
                requested.startDate,
                requested.duration,
                pet,
                requested.price,
                office,
                vet
        );
        Mockito.when(vetsRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(vet));
        var expected = Response.errorResponse(ResponseErrorMessage.VISIT_TIME_UNAVAILABLE);
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
        var expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        var uut = new VisitsService(visitsRepository, petsRepository, officesRepository, vetsRepository, clock);

        var result = uut.addVisit(requested);

        assertThat(result).isEqualTo(expected);
        Mockito.verify(visitsRepository, Mockito.times(0)).save(Mockito.any(VisitRecord.class));
        Mockito.verify(petsRepository, Mockito.times(1)).findById(pet.id);
        Mockito.verify(officesRepository, Mockito.times(0)).findById(Mockito.any(Integer.class));
        Mockito.verify(vetsRepository, Mockito.times(1)).findById(vet.id);
    }
}
