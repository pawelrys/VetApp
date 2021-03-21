package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {

    @Query("select v from visits v where ((:start >= v.startDate and :start < (v.startDate + v.duration)) or (:start < v.startDate and :end > v.startDate))")
    List<VisitRecord> getRecordsInTime(LocalDateTime start, LocalDateTime end);
}
