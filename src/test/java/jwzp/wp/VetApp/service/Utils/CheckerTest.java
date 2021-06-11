package jwzp.wp.VetApp.service.Utils;

import jwzp.wp.VetApp.models.dtos.*;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.models.values.Status;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CheckerTest {

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/missingVisitDataTestInput.csv", numLinesToSkip = 1)
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
        var requested = new VisitData(startDate, duration, petId, Status.PENDING, price, officeId, vetId, "");

        assert Checker.getMissingData(requested).isEmpty() == result;
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/missingVetDataTestInput.csv", numLinesToSkip = 1)
    public void testMissingVetData(String testCaseName, String name, String surname, String photo, LocalTime start, LocalTime end, boolean result) throws Exception {

        var requested = new VetData(name, surname, photo == null ? null : photo.getBytes(StandardCharsets.UTF_8), start, end);

        assert Checker.getMissingData(requested).isEmpty() == result;
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/missingPetDataTestInput.csv", numLinesToSkip = 1)
    public void testMissingPetData(String testCaseName, String name, LocalDate birthday, Animal animal, Integer ownerId, Boolean result) throws Exception {
        var requested = new PetData(name, birthday, animal, ownerId);

        assert Checker.getMissingData(requested).isEmpty() == result;
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/missingClientDataTestInput.csv", numLinesToSkip = 1)
    public void testMissingClientData(String testCaseName, String name, String surname, boolean result) {
        var requested = new ClientData(name, surname);

        assert Checker.getMissingData(requested).isEmpty() == result;
    }

    @ParameterizedTest(name="{0}")
    @CsvFileSource(resources = "/jwzp.wp.VetApp/service/missingOfficeDataTestInput.csv", numLinesToSkip = 1)
    public void testMissingOfficeData(String testCaseName, String name, boolean result) throws Exception {
        var requested = new OfficeData(name);

        assert Checker.getMissingData(requested).isEmpty() == result;
    }
}

