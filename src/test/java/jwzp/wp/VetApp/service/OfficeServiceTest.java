package jwzp.wp.VetApp.service;

import jwzp.wp.VetApp.models.dtos.OfficeData;
import jwzp.wp.VetApp.models.records.OfficeRecord;
import jwzp.wp.VetApp.resources.OfficesRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessageFormatter;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class OfficeServiceTest {

    @Mock
    OfficesRepository officesRepository;

    @Test
    public void testAddOfficePositive() throws Exception {
        OfficeData requested = new OfficeData("Pinokio");
        Response<OfficeRecord> expected = Response.succeedResponse(new OfficeRecord(1, "Pinokio"));
        Mockito.when(officesRepository.save(Mockito.any(OfficeRecord.class))).thenReturn(expected.get());
        var uut = new OfficesService(officesRepository);

        var result = uut.addOffice(requested);

        assert expected.equals(result);
        Mockito.verify(officesRepository, Mockito.times(1)).save(OfficeRecord.createOfficeRecord(requested.name));
    }

    @Test
    public void testAddOfficeMissingData() throws Exception {
        OfficeData requested = new OfficeData(null);
        var errorBuilder = new ErrorMessagesBuilder();
        errorBuilder.addToMessage(ErrorMessageFormatter.missingField("name"));
        var error = errorBuilder.build(ErrorType.WRONG_ARGUMENTS);
        var expected = Response.errorResponse(error);
        var uut = new OfficesService(officesRepository);

        var result = uut.addOffice(requested);

        assert result.equals(expected);
        Mockito.verify(officesRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    public void testAddOfficeRepositoryException() throws Exception {
        OfficeData requested = new OfficeData("Pinokio");
        Response<OfficeRecord> expected = Response.errorResponse(ErrorMessagesBuilder.simpleError(ErrorType.WRONG_ARGUMENTS));
        Mockito.when(officesRepository.save(Mockito.any(OfficeRecord.class))).thenThrow(new IllegalArgumentException());
        var uut = new OfficesService(officesRepository);

        var result = uut.addOffice(requested);

        assert result.equals(expected);
        Mockito.verify(officesRepository, Mockito.times(1)).save(OfficeRecord.createOfficeRecord(requested.name));
    }
}
