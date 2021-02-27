package jwzp.wp.VetApp.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDate;

@Entity(name="Visit")
public class VisitRecord {

    @Id
    private final int id;
    public LocalDate startDate;
    public Duration duration;
    public Animal animalKind;
    public Status status;

    public VisitRecord(){
        this.id = -1;
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
