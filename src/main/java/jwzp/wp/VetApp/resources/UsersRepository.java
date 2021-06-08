package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UserRecord, Integer> {

    @Query("select v from users v where v.username = :username")
    Optional<UserRecord> findByName(String username);
}
