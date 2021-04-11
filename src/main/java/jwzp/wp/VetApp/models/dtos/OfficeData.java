package jwzp.wp.VetApp.models.dtos;

import java.util.Objects;

public class OfficeData {
    public String name;

    public OfficeData(){}

    public OfficeData(
            String name
    ) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficeData that = (OfficeData) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
