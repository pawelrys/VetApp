package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {


}
