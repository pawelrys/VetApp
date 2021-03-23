package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.models.values.Status;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name="visits")
@TypeDef(typeClass = PostgreSQLIntervalType.class, defaultForType = Duration.class)
public class VisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    public LocalDateTime startDate;
    @Column(columnDefinition = "interval")
    public Duration duration;

    @OneToOne
    @JoinTable(name="pet")
    public PetRecord pet;
    public Status status;
    public BigDecimal price;

    protected VisitRecord(){
        this.id = -1;
    }

    public VisitRecord(
            int id,
            LocalDateTime startDate,
            Duration duration,
            PetRecord pet,
            Status status,
            BigDecimal price
    ) {
        this.id = id;
        this.startDate = startDate;
        this.duration = duration;
        this.pet = pet;
        this.status = status;
        this.price = price;
    }

    public static VisitRecord createNewVisit(VisitData data) {
        VisitRecord newVisit = new VisitRecord();
        newVisit.startDate = data.startDate;
        newVisit.duration = data.duration;
        newVisit.pet = data.pet;
        newVisit.status = Status.PENDING;
        newVisit.price = data.price;
        return newVisit;
    }

    public void update(VisitData data) {
        if (data.startDate != null) {
            this.startDate = data.startDate;
        }
        if (data.duration != null) {
            this.duration = data.duration;
        }
        if (data.pet != null) {
            this.pet = data.pet;
        }
        if (data.status != null) {
            this.status = data.status;
        }
        if (data.price != null) {
            this.price = data.price;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitRecord record = (VisitRecord) o;
        return id == record.id && startDate.equals(record.startDate) && duration.equals(record.duration) && pet == record.pet && status == record.status && price.equals(record.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, duration, pet, status, price);
    }

    public int getId() {
        return id;
    }
}
