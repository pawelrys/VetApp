package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.values.Animal;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name="pets")
public class PetRecord extends RepresentationModel<PetRecord> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public final String name;
    public final LocalDate birthday;
    public final Animal animal;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    public final ClientRecord owner;

    protected PetRecord(){
        this.id = -1;
        this.name = "";
        this.birthday = LocalDate.EPOCH;
        this.animal = Animal.Dog;
        this.owner = new ClientRecord();
    }

    private PetRecord(
            String name,
            LocalDate birthday,
            Animal animal,
            ClientRecord owner
    ) {
        this.id = -1;
        this.name = name;
        this.birthday = birthday;
        this.animal = animal;
        this.owner = owner;
    }

    public PetRecord(
            int id,
            String name,
            LocalDate birthday,
            Animal animal,
            ClientRecord owner
    ) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.animal = animal;
        this.owner = owner;
    }

    public static PetRecord createPetRecord(String name, LocalDate birthday, Animal animal, ClientRecord owner) {
        return new PetRecord(name, birthday, animal, owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetRecord that = (PetRecord) o;
        return id == that.id && name.equals(that.name) && birthday.equals(that.birthday) && animal.equals(that.animal) && owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthday, animal, owner);
    }
}
