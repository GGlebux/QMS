package most.qms.repositories;

import most.qms.models.DailyCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyCounterRepository extends JpaRepository<DailyCounter, LocalDate> {

}
