package jwzp.wp.VetApp.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDate;

@Entity(name="visits")
public class VisitRecord {

    @Id
    private final int id;
    public LocalDate startDate;
    public Duration duration;
    public Animal animalKind;
    public Status status;

    protected VisitRecord(){
        this.id = -1;
    }

    public VisitRecord(
            int id,
            LocalDate startDate,
            Duration duration,
            Animal animalKind,
            Status status
    ){
        this.id = id;
        this.startDate = startDate;
        this.duration = duration;
        this.animalKind = animalKind;
        this.status = status;
    }

    public VisitRecord(VisitData data){
        this.id = -1;
        this.startDate = data.startDate;
        this.duration = data.duration;
        this.animalKind = data.animalKind;
        this.status = Status.Pending;
    }
    
    @Override
    public boolean equals(Object other){
        if (!(other instanceof VisitRecord)) {
            return false;
        }
        var otherVisit = (VisitRecord) other;
        return this.id == otherVisit.id
                && this.startDate == otherVisit.startDate
                && this.duration == otherVisit.duration
                && this.animalKind == otherVisit.animalKind
                && this.status == otherVisit.status;
    }

    @Override
    public int hashCode(){
        return this.id
                + this.startDate.hashCode()
                + this.duration.hashCode()
                + this.animalKind.hashCode()
                + this.status.hashCode();
    }

}
