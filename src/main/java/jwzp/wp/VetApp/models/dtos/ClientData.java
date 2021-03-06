package jwzp.wp.VetApp.models.dtos;


import java.util.Objects;

public class ClientData {
    public final String name;
    public final String surname;

    public ClientData() {
        this.name = null;
        this.surname = null;
    }

    public ClientData(
            String name,
            String surname
    ){
        this.name = name;
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData that = (ClientData) o;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname);
    }
}
