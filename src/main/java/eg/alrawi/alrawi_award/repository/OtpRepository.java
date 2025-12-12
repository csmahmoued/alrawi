package eg.alrawi.alrawi_award.repository;

import eg.alrawi.alrawi_award.entity.AlrawiOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<AlrawiOtp,Long> {

    Optional<AlrawiOtp> findByEmailAndOtp(String email,String otp);
}
