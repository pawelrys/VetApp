package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.values.Animal;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name="pets")
public class PetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public String name;
    public LocalDate birthday;
    public Animal animal;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private ClientRecord owner;

    protected PetRecord(){
        this.id = -1;
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

    public static PetRecord createPetRecord(PetData data) {
        var pet = new PetRecord();
        pet.name = data.name;
        pet.birthday = data.birthday;
        pet.animal = data.animal;
        pet.owner = data.owner;
        return pet;
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
