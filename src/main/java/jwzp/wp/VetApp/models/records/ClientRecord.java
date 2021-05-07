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
    public String name;
    public String surname;

    @OneToMany(mappedBy = "owner")
    private final List<PetRecord> pets = new ArrayList<>();

    protected ClientRecord(){
        this.id = -1;
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

    public static ClientRecord createClientRecord(ClientData data){
        var client = new ClientRecord();
        client.name = data.name;
        client.surname = data.surname;
        return client;
    }

    public void update(ClientData data) {
        if (data.name != null) {
            name = data.name;
        }
        if (data.surname != null) {
            surname = data.surname;
        }
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
