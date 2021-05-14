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
    public final LocalDateTime startDate;
    @Column(columnDefinition = "interval")
    public final Duration duration;

    @OneToOne
    @JoinTable(name="pet")
    public final PetRecord pet;
    public final Status status;
    public final BigDecimal price;

    @OneToOne
    @JoinTable(name = "office")
    public final OfficeRecord office;

    @OneToOne
    @JoinTable(name = "vet")
    public final VetRecord vet;

    protected VisitRecord(){
        this.id = -1;
        this.startDate = LocalDateTime.now();
        this.duration = Duration.ZERO;
        this.pet = new PetRecord();
        this.status = Status.CLOSED_AUTOMATICALLY;
        this.price = BigDecimal.ZERO;
        this.office = new OfficeRecord();
        this.vet = new VetRecord();
    }

    private VisitRecord(
            LocalDateTime startDate,
            Duration duration,
            PetRecord pet,
            Status status,
            BigDecimal price,
            OfficeRecord office,
            VetRecord vet
    ) {
        this.id = -1;
        this.startDate = startDate;
        this.duration = duration;
        this.pet = pet;
        this.status = status;
        this.price = price;
        this.office = office;
        this.vet = vet;
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

    public static VisitRecord createVisitRecord(
            LocalDateTime startDate,
            Duration duration,
            PetRecord pet,
            BigDecimal price,
            OfficeRecord office,
            VetRecord vet
    ) {
        return new VisitRecord(startDate, duration, pet, Status.PENDING, price, office, vet);
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
