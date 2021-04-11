package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.OfficeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficesRepository extends JpaRepository<OfficeRecord, Integer> {}
