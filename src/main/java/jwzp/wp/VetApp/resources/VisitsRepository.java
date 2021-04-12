package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.values.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {

    @Query("select v from visits v where ((:start >= v.startDate and :start < (v.startDate + v.duration)) or (:start < v.startDate and :end > v.startDate))")
    List<VisitRecord> getRecordsInTime(LocalDateTime start, LocalDateTime end);

    @Query(value = "with slots as (\n" +
            "   select generate_series as start, generate_series + cast('15 minutes' as interval) as \"end\" " +
            "   from generate_series(cast(?1 as timestamp), cast(?2 as timestamp), cast('15 minutes' as interval))\n" +
            "),\n" +
            "unavailable as (\n" +
            "   select start_date as start, start_date + duration as \"end\" from visits\n" +
            "),\n" +
            "starts_to_remove as (\n" +
            "   select slots.start from slots, unavailable\n" +
            "   where slots.start between unavailable.start and unavailable.end\n" +
            "       or unavailable.start between slots.start and slots.end\n" +
            "),\n" +
            "final as (\n" +
            "   select slots.start, slots.end from slots\n" +
            "   where slots.start not in (select * from starts_to_remove)\n" +
            ")\n" +
            "select * from final;", nativeQuery = true)
    List<Timestamp[]> getAvailableTimeSlots(LocalDateTime start, LocalDateTime end);

    @Query("select v from visits v where :now > v.startDate + v.duration and :status = v.status")
    List<VisitRecord> getPastVisitsWithStatus(LocalDateTime now, Status status);
}
