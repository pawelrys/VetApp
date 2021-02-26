package jwzp.wp.VetApp.resources;


import java.time.Duration;
import java.time.LocalDate;

public class Visits {

    public LocalDate startDate;
    public Duration duration;
    public Animal animalKind;

    @Override
    public boolean equals(Object other){
        if (!(other instanceof Visits)) {
            return false;
        }
        var visits = (Visits) other;
        return this.startDate == visits.startDate
                && this.duration == visits.duration
                && this.animalKind == visits.animalKind;
    }

    @Override
    public int hashCode(){
        return this.startDate.hashCode() + this.duration.hashCode() + this.animalKind.hashCode();
    }

}
