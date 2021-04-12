package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.VisitData;
import jwzp.wp.VetApp.models.values.Status;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name="visits")
@TypeDef(typeClass = PostgreSQLIntervalType.class, defaultForType = Duration.class)
public class VisitRecord extends RepresentationModel<VisitRecord> {

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

    @OneToOne
    @JoinTable(name = "office")
    public OfficeRecord office;

    @OneToOne
    @JoinTable(name = "vet")
    public VetRecord vet;

    protected VisitRecord(){
        this.id = -1;
    }

    public VisitRecord(
            int id,
            LocalDateTime startDate,
            Duration duration,
            PetRecord pet,
            Status status,
            BigDecimal price,
            OfficeRecord office,
            VetRecord vet
    ) {
        this.id = id;
        this.startDate = startDate;
        this.duration = duration;
        this.pet = pet;
        this.status = status;
        this.price = price;
        this.office = office;
        this.vet = vet;
    }

    public static VisitRecord createNewVisit(
            LocalDateTime startDate,
            Duration duration,
            PetRecord pet,
            BigDecimal price,
            OfficeRecord office,
            VetRecord vet
    ) {
        VisitRecord newVisit = new VisitRecord();
        newVisit.startDate = startDate;
        newVisit.duration = duration;
        newVisit.pet = pet;
        newVisit.status = Status.PENDING;
        newVisit.price = price;
        newVisit.office = office;
        newVisit.vet = vet;
        return newVisit;
    }

    public void update(VisitData data) {
        if (data.startDate != null) {
            this.startDate = data.startDate;
        }
        if (data.duration != null) {
            this.duration = data.duration;
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
        return id == record.id && startDate.equals(record.startDate) && duration.equals(record.duration) && pet == record.pet && status == record.status && price.equals(record.price) && office == record.office && vet == record.vet;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, duration, pet, status, price, office, vet);
    }

    public int getId() {
        return id;
    }
}
