package jwzp.wp.VetApp.models.dtos;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

public class VetData {
    public String name;
    public String surname;
    public byte[] photo;
    public LocalTime start;
    public LocalTime end;

    public VetData(
            String name,
            String surname,
            byte[] photo,
            LocalTime start,
            LocalTime end
    ) {
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VetData that = (VetData) o;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Arrays.equals(photo, that.photo) && Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, photo, start, end);
    }
}
