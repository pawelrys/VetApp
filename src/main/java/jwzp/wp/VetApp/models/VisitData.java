package jwzp.wp.VetApp.models;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class VisitData {
    public final LocalDate startDate;
    public final Duration duration;
    public final Animal animalKind;

    public VisitData(
            LocalDate startDate,
            Duration duration,
            Animal animalKind
    ){
        this.startDate = startDate;
        this.duration = duration;
        this.animalKind = animalKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitData that = (VisitData) o;
        return Objects.equals(startDate, that.startDate)
                && Objects.equals(duration, that.duration)
                && animalKind == that.animalKind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, duration, animalKind);
    }
}
