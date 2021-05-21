package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
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
        Mockito.verify(vetsRepository, Mockito.times(1)).save(VetRecord.createVetRecord(requested.name, requested.surname, requested.photo, requested.officeHoursStart, requested.officeHoursEnd));
    }

    @Test
    public void testAddVetMissingData() throws Exception {
        VetData requested = new VetData(null, null, null, null, null);

        var errorBuilder = new ErrorMessagesBuilder();
        errorBuilder.addToMessage("name");
        errorBuilder.addToMessage("surname");
        errorBuilder.addToMessage("photo");
        errorBuilder.addToMessage("officeHoursStart");
        errorBuilder.addToMessage("officeHoursEnd");
        var error = errorBuilder.build("Missing fields: ");
        var expected = Response.errorResponse(error);
        var uut = new VetsService(vetsRepository);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsRepository, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    public void testAddVetRepositoryException() throws Exception {
        VetData requested = new VetData("Jan", "Ptak", new byte[0], LocalTime.parse("08:00:00"), LocalTime.parse("18:00:00"));
        Response<VetRecord> expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        Mockito.when(vetsRepository.save(Mockito.any(VetRecord.class))).thenThrow(new IllegalArgumentException());
        var uut = new VetsService(vetsRepository);

        var result = uut.addVet(requested);

        assert result.equals(expected);
        Mockito.verify(vetsRepository, Mockito.times(1)).save(VetRecord.createVetRecord(requested.name, requested.surname, requested.photo, requested.officeHoursStart, requested.officeHoursEnd));
    }
}
