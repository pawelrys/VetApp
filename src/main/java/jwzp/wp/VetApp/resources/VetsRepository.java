package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.VetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VetsRepository extends JpaRepository<VetRecord, Integer> {
}
