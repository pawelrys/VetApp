package jwzp.wp.VetApp.models.records;


import jwzp.wp.VetApp.models.dtos.OfficeData;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "offices")
public class OfficeRecord extends RepresentationModel<OfficeRecord> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public final String name;

    protected OfficeRecord() {
        this.id = -1;
        this.name = "";
    }

    private OfficeRecord(String name) {
        this.id = -1;
        this.name = name;
    }

    public OfficeRecord(
            int id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static OfficeRecord createOfficeRecord(String name) {
        return new OfficeRecord(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficeRecord that = (OfficeRecord) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
