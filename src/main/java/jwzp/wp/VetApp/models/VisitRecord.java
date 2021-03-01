package jwzp.wp.VetApp.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name="visits")
public class VisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    public LocalDate startDate;
    public Duration duration;
    public Animal animalKind;
    public Status status;
    public BigDecimal price;

    protected VisitRecord(){
        this.id = -1;
    }

    public VisitRecord(
            int id,
            LocalDate startDate,
            Duration duration,
            Animal animalKind,
            Status status,
            BigDecimal price
    ){
        this.id = id;
        this.startDate = startDate;
        this.duration = duration;
        this.animalKind = animalKind;
        this.status = status;
        this.price = price;
    }

    public static VisitRecord createNewVisit(VisitData data){
        VisitRecord newVisit = new VisitRecord();
        newVisit.startDate = data.startDate;
        newVisit.duration = data.duration;
        newVisit.animalKind = data.animalKind;
        newVisit.status = Status.Pending;
        newVisit.price = data.price;
        return newVisit;
    }

    public void update(VisitData data){
        if (data.startDate != null) {
            this.startDate = data.startDate;
        }
        if (data.duration != null) {
            this.duration = data.duration;
        }
        if (data.animalKind != null) {
            this.animalKind = data.animalKind;
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
        return id == record.id && startDate.equals(record.startDate) && duration.equals(record.duration) && animalKind == record.animalKind && status == record.status && price.equals(record.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, duration, animalKind, status, price);
    }

    public int getId() {
        return id;
    }
}
