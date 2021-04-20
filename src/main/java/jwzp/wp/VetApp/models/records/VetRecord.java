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
    public String name;
    public String surname;
    public byte[] photo;
    @Column(name = "officeHoursEnd", columnDefinition = "TIME")
    public LocalTime officeHoursEnd;
    @Column(name = "officeHoursStart", columnDefinition = "TIME")
    public LocalTime officeHoursStart;

    public VetRecord() {
        this.id = -1;
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

    public static VetRecord createVetRecord(VetData data) {
        var vet = new VetRecord();
        vet.name = data.name;
        vet.surname = data.surname;
        vet.photo = data.photo;
        vet.officeHoursStart = data.officeHoursStart;
        vet.officeHoursEnd = data.officeHoursEnd;
        return vet;
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
        return Objects.hash(id, name, surname, photo, officeHoursStart, officeHoursEnd);
    }
}

