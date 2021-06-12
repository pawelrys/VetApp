package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetsRepository extends JpaRepository<PetRecord, Integer> {

    @Query("select p from pets p where p.owner.id = :clientId")
    List<PetRecord> getPetByClient(int clientId);
}
