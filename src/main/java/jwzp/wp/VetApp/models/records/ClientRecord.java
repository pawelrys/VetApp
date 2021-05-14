package jwzp.wp.VetApp.models.records;

import jwzp.wp.VetApp.models.dtos.ClientData;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name="clients")
public class ClientRecord extends RepresentationModel<ClientRecord> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public final int id;
    public final String name;
    public final String surname;

    @OneToMany(mappedBy = "owner")
    private final List<PetRecord> pets = new ArrayList<>();

    protected ClientRecord(){
        this.id = -1;
        this.name = "";
        this.surname = "";
    }

    private ClientRecord(String name, String surname){
        this.id = -1;
        this.name = name;
        this.surname = surname;
    }

    public ClientRecord(
            int id,
            String name,
            String surname
    ) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public static ClientRecord createClientRecord(String name, String surname){
        return new ClientRecord(name, surname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRecord that = (ClientRecord) o;
        return id == that.id && name.equals(that.name) && surname.equals(that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }
}
