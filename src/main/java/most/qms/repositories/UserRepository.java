package most.qms.repositories;

import most.qms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    @Query(
            "SELECT (count(u) > 0) " +
            "FROM User u " +
            "WHERE u.phoneNumber = :phoneNumber " +
            "AND u.status = most.qms.models.UserStatus.ACTIVE"
    )
    boolean existsActiveByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
