package most.qms.repositories;

import most.qms.models.ResetPassword;
import most.qms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    Optional<ResetPassword> findByToken(String token);
    void deleteByToken(String token);
    void deleteAllByUser(User user);
}
