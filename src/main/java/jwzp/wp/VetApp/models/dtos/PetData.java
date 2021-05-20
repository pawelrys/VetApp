package jwzp.wp.VetApp.models.dtos;

import jwzp.wp.VetApp.models.values.Animal;

import java.time.LocalDate;
import java.util.Objects;

public class PetData {
    public final String name;
    public final LocalDate birthday;
    public final Animal animal;
    public final Integer ownerId;

    public PetData() {
        this.name = null;
        this.birthday = null;
        this.animal = null;
        this.ownerId = null;
    }

    public PetData(
            String name,
            LocalDate birthday,
            Animal animal,
            Integer ownerId
    ) {
        this.name = name;
        this.birthday = birthday;
        this.animal = animal;
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetData that = (PetData) o;
        return Objects.equals(name, that.name) && Objects.equals(birthday, that.birthday) && Objects.equals(animal, that.animal) && Objects.equals(ownerId, that.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthday, animal, ownerId);
    }
}
