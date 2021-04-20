package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;


@ExtendWith(MockitoExtension.class)
public class VetServiceTest {

    @Mock
    VetsRepository vetsRepository;

    @Test
    public void testAddVetPositive() throws Exception {
        VetData requested = new VetData("Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        Response<VetRecord> expected = Response.succeedResponse(new VetRecord(1, "Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00")));
        Mockito.when(vetsRepository.save(Mockito.any(VetRecord.class))).thenReturn(expected.get());
        var uut = new VetsService(vetsRepository);

        var result = uut.addVet(requested);

        assert expected.equals(result);
        Mockito.verify(vetsRepository, Mockito.times(1)).save(VetRecord.createVetRecord(requested));
    }

    @Test
    public void testAddVetMissingData() throws Exception {
        VetData requested = new VetData(null, null, null, null, null);
        Response<?> expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        var uut = new VetsService(vetsRepository);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsRepository, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    public void testAddVetRepositoryException() throws Exception {
        VetData requested = new VetData("Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        Response<VetRecord> expected = Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS);
        Mockito.when(vetsRepository.save(Mockito.any(VetRecord.class))).thenThrow(new IllegalArgumentException());
        var uut = new VetsService(vetsRepository);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsRepository, Mockito.times(1)).save(VetRecord.createVetRecord(requested));
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/ableToCreateFromDataTestVet.csv", numLinesToSkip = 1)
    public void testAbleToCreateFromData(String testCaseName, String name, String surname, String photo, LocalTime start, LocalTime end, boolean result) throws Exception {

        var requested = new VetData(name, surname, photo == null ? null : photo.getBytes(StandardCharsets.UTF_8), start, end);
        var uut = new VetsService(vetsRepository);

        assert uut.ableToCreateFromData(requested) == result;
    }
}
