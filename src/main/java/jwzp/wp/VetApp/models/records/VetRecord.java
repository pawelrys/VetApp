package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.VetData;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

@Entity(name="vets")
public class VetRecord extends RepresentationModel<VetRecord> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public final String name;
    public final String surname;
    public final byte[] photo;
    @Column(name = "officeHoursEnd", columnDefinition = "TIME")
    public final LocalTime officeHoursEnd;
    @Column(name = "officeHoursStart", columnDefinition = "TIME")
    public final LocalTime officeHoursStart;

    public VetRecord() {
        this.id = -1;
        this.name = "";
        this.surname = "";
        this.photo = new byte[0];
        this.officeHoursStart = LocalTime.NOON;
        this.officeHoursEnd = LocalTime.NOON;
    }

    private VetRecord(
            String name,
            String surname,
            byte[] photo,
            LocalTime officeHoursStart,
            LocalTime officeHoursEnd
    ) {
        this.id = -1;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.officeHoursStart = officeHoursStart;
        this.officeHoursEnd = officeHoursEnd;
    }

    public VetRecord(
            int id,
            String name,
            String surname,
            byte[] photo,
            LocalTime officeHoursStart,
            LocalTime officeHoursEnd
    ) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.officeHoursStart = officeHoursStart;
        this.officeHoursEnd = officeHoursEnd;
    }

    public static VetRecord createVetRecord(String name, String surname, byte[] photo, LocalTime officeHoursStart, LocalTime officeHoursEnd) {
        return new VetRecord(name, surname, photo, officeHoursStart, officeHoursEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VetRecord that = (VetRecord) o;
        return id == that.id && name.equals(that.name) && surname.equals(that.surname) && officeHoursStart.equals(that.officeHoursStart) && officeHoursEnd.equals(that.officeHoursEnd) && Arrays.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, Arrays.hashCode(photo), officeHoursStart, officeHoursEnd);
    }
}

