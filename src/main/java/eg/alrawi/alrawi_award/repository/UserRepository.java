package eg.alrawi.alrawi_award.repository;


import eg.alrawi.alrawi_award.entity.AlrawiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AlrawiUser,Long> {
    Optional<AlrawiUser> findByUsername(String username);
    Optional<AlrawiUser> findByEmail(String email);
    Optional<AlrawiUser> findByNationalId(String email);
    Optional<AlrawiUser> findByPassportNumber(String email);

}
