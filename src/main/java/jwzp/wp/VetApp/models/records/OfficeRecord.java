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
    public String name;

    protected OfficeRecord() {
        this.id = -1;
    }

    public OfficeRecord(
            int id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static OfficeRecord createOfficeRecord(OfficeData data) {
        var office = new OfficeRecord();
        office.name = data.name;
        return office;
    }

    public void update(OfficeData data) {
        if (data.name != null) {
            name = data.name;
        }
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
