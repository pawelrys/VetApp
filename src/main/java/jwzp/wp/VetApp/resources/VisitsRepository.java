package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.values.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {

    @Query("select v from visits v where (((:start >= v.startDate and :start < (v.startDate + v.duration)) or (:start < v.startDate and :end > v.startDate)) and (:officeId = v.office.id or :vetId = v.vet.id))")
    List<VisitRecord> getRecordsInTimeOfficeVet(LocalDateTime start, LocalDateTime end, Integer officeId, Integer vetId);

    @Query("select v from visits v where :now > v.startDate + v.duration and :status = v.status")
    List<VisitRecord> getPastVisitsWithStatus(LocalDateTime now, Status status);
}
