package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {


    @Query("select v from visits v where (:start >= v.startDate and :start < (v.startDate + v.duration)) or (:duration > v.startDate and :start <= (v.startDate + v.duration))")
    List<VisitRecord> getRecordsInTime(LocalDate start, Duration duration);
}
