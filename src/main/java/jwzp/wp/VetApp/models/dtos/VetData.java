package jwzp.wp.VetApp.models.dtos;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

public class VetData {
    public final String name;
    public final String surname;
    public final byte[] photo;
    public final LocalTime officeHoursStart;
    public final LocalTime officeHoursEnd;

    public VetData() {
        this.name = null;
        this.surname = null;
        this.photo = null;
        this.officeHoursStart = null;
        this.officeHoursEnd = null;
    }

    public VetData(
            String name,
            String surname,
            byte[] photo,
            LocalTime officeHoursStart,
            LocalTime officeHoursEnd
    ) {
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.officeHoursStart = officeHoursStart;
        this.officeHoursEnd = officeHoursEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VetData that = (VetData) o;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Arrays.equals(photo, that.photo) && Objects.equals(officeHoursStart, that.officeHoursStart) && Objects.equals(officeHoursEnd, that.officeHoursEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, Arrays.hashCode(photo), officeHoursStart, officeHoursEnd);
    }
}
