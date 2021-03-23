package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.PetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetsRepository extends JpaRepository<PetRecord, Integer> {
}
