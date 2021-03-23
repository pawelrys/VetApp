package jwzp.wp.VetApp.models.dtos;

import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.values.Animal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Objects;

public class PetData {
    public String name;
    public LocalDate birthday;
    public Animal animal;
    public ClientRecord owner;

    @PersistenceContext
    EntityManager entityManager;

    public PetData(
            String name,
            LocalDate birthday,
            Animal animal,
            int ownerId
    ) {
        this.name = name;
        this.birthday = birthday;
        this.animal = animal;
        this.owner = entityManager.find(ClientRecord.class, ownerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetData that = (PetData) o;
        return Objects.equals(name, that.name) && Objects.equals(birthday, that.birthday) && Objects.equals(animal, that.animal) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthday, animal, owner);
    }
}
