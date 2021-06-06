package jwzp.wp.VetApp.resources;

import jwzp.wp.VetApp.models.records.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UserRecord, Integer> {

}
