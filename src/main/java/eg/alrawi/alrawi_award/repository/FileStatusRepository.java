package eg.alrawi.alrawi_award.repository;

import eg.alrawi.alrawi_award.entity.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStatusRepository  extends JpaRepository<FileStatus,String> {
}
