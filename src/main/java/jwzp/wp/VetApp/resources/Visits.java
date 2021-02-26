package jwzp.wp.VetApp.resources;


import java.time.Duration;
import java.time.LocalDate;

public class Visits {

    private final int id;
    public LocalDate startDate;
    public Duration duration;
    public Animal animalKind;
    public Status status;

    public Visits(int id){
        this.id = id;
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof Visits)) {
            return false;
        }
        var otherVisit = (Visits) other;
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
