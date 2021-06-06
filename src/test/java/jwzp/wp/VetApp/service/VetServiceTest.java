package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.records.VetRecord;
import jwzp.wp.VetApp.resources.VetsRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessageFormatter;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("name"));
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("surname"));
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("photo"));
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("officeHoursStart"));
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("officeHoursEnd"));
        var error = errorBuilder.build(ErrorType.WRONG_ARGUMENTS);
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
