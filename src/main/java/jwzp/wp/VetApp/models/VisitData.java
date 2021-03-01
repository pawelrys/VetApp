package jwzp.wp.VetApp.models;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class VisitData {
    public final LocalDate startDate;
    public final Duration duration;
    public final Animal animalKind;
    public final Status status;
    public BigDecimal price;

    public VisitData(
            LocalDate startDate,
            Duration duration,
            Animal animalKind,
            Status status,
            BigDecimal price
    ){
        this.startDate = startDate;
        this.duration = duration;
        this.animalKind = animalKind;
        this.status = status;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitData visitData = (VisitData) o;
        return Objects.equals(startDate, visitData.startDate) && Objects.equals(duration, visitData.duration) && animalKind == visitData.animalKind && status == visitData.status && Objects.equals(price, visitData.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, duration, animalKind, status, price);
    }
}
