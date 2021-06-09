package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.VisitRecord;
import jwzp.wp.VetApp.models.utils.VetsTimeInterval;
import jwzp.wp.VetApp.models.values.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface VisitsRepository extends JpaRepository<VisitRecord, Integer> {

    @Query("select v from visits v where (((:start >= v.startDate and :start < (v.startDate + v.duration)) or (:start < v.startDate and :end > v.startDate)) and (:officeId = v.office.id or :vetId = v.vet.id))")
    List<VisitRecord> getRegisteredVisitsInTime(LocalDateTime start, LocalDateTime end, Integer officeId, Integer vetId);

    @Query(value = "with time_slots as (\n" +
            "   select generate_series as start, generate_series + cast('15 minutes' as interval) as \"end\" " +
            "   from generate_series(cast(?1 as timestamp), cast(?2 as timestamp), cast('15 minutes' as interval))\n" +
            "),\n" +
            "vets as (\n" +
            "    select id as vet_id from vets\n" +
            "),\n" +
            "slots as (\n" +
            "    select start, \"end\", vet_id\n" +
            "    from time_slots\n" +
            "    cross join vets\n" +
            "),\n" +
            "unavailable as (\n" +
            "    select start_date as start, start_date + duration as \"end\", v.vet_id from visits\n" +
            "    join vet v using(id)\n" +
            "),\n" +
            "to_remove as (\n" +
            "    select slots.start, vet_id from slots\n" +
            "    join unavailable using(vet_id)\n" +
            "    where slots.start between unavailable.start and unavailable.end\n" +
            "        or unavailable.start between slots.start and slots.end\n" +
            "),\n" +
            "final as (\n" +
            "    select slots.start, slots.end, vet_id from slots\n" +
            "    where (slots.start, vet_id) not in (select * from to_remove)\n" +
            ")\n" +
            "select * from final;", nativeQuery = true)
    List<Object[]> getAvailableTimeSlotsAux(LocalDateTime start, LocalDateTime end);

    default List<VetsTimeInterval> getAvailableTimeSlots(LocalDateTime start, LocalDateTime end){
        List<Object[]> slots = getAvailableTimeSlotsAux(start, end);
        return slots.stream().map(t ->
                new VetsTimeInterval(
                        ((Timestamp)t[0]).toLocalDateTime(),
                        ((Timestamp)t[1]).toLocalDateTime(),
                        Collections.singletonList((Integer)t[2])
                )
        ).collect(Collectors.toList());
    }


    @Query("select v from visits v where :now > v.startDate + v.duration and :status = v.status")
    List<VisitRecord> getPastVisitsWithStatus(LocalDateTime now, Status status);

    @Query("select v from visits v where v.pet.owner.id = :clientId")
    List<VisitRecord> getVisitsByClient(int clientId);

    @Query("select v from visits v where v.vet.id = :vetId")
    List<VisitRecord> getVisitsByVet(int vetId);
}
