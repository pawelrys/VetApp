package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.VetData;
import jwzp.wp.VetApp.models.values.OpeningHours;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

@Entity(name="vet")
public class VetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public String name;
    public String surname;
    public byte[] photo;
    @Column(name = "startTime", columnDefinition = "TIME")
    public LocalTime end;
    @Column(name = "endTime", columnDefinition = "TIME")
    public LocalTime start;

    public VetRecord() {
        this.id = -1;
    }

    public VetRecord(
            int id,
            String name,
            String surname,
            byte[] photo,
            LocalTime start,
            LocalTime end
    ) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.start = start;
        this.end = end;
    }

    public VetRecord createVet(VetData data) {
        var vet = new VetRecord();
        vet.name = data.name;
        vet.surname = data.surname;
        vet.photo = data.photo;
        vet.start = data.start;
        vet.end = data.end;
        return vet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VetRecord that = (VetRecord) o;
        return id == that.id && name.equals(that.name) && surname.equals(that.surname) && start.equals(that.start) && end.equals(that.end) && Arrays.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, photo, start, end);
    }
}

