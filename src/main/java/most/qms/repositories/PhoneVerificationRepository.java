package most.qms.repositories;

import most.qms.models.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    void deleteByPhoneNumber(String phoneNumber);

    Optional<PhoneVerification> findByPhoneNumber(String phoneNumber);
}
