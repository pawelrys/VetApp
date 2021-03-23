package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.ClientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsRepository extends JpaRepository<ClientRecord, Integer> {

    @Query("select c from clients c where c.id = :id")
    ClientRecord getClientById(int id);
}
