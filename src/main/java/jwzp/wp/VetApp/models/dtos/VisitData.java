package jwzp.wp.VetApp.models.dtos;

import jwzp.wp.VetApp.models.values.Status;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class VisitData {
    public final LocalDateTime startDate;
    public final Duration duration;
    public final Integer petId;
    public final Status status;
    public BigDecimal price;
    public Integer officeId;

    public VisitData(
            LocalDateTime startDate,
            Duration duration,
            int petId,
            Status status,
            BigDecimal price,
            Integer officeId
    ) throws NullPointerException {
        this.startDate = startDate;
        this.duration = duration;
        this.petId = petId;
        this.status = status;
        this.price = price;
        this.officeId = officeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitData visitData = (VisitData) o;
        return Objects.equals(startDate, visitData.startDate) && Objects.equals(duration, visitData.duration) && Objects.equals(petId, visitData.petId) && status == visitData.status && Objects.equals(price, visitData.price) && Objects.equals(officeId, visitData.officeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, duration, petId, status, price, officeId);
    }
}
