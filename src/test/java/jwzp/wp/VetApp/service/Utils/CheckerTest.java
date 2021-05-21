package jwzp.wp.VetApp.service.Utils;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.values.Status;
import jwzp.wp.VetApp.service.VisitsService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class CheckerTest {

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

        assert Checker.getMissingData(requested).isEmpty() == result;
    }

}

