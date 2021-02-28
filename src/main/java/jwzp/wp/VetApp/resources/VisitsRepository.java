package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {

    @Query("select v from visits v")
    List<VisitRecord> overlap();
}
