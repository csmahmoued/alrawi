package eg.alrawi.alrawi_award.repository;

import eg.alrawi.alrawi_award.entity.AlrawiProjectContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AlrawiProjectContentRepository extends JpaRepository<AlrawiProjectContent,Long> {

    @Query(value = "select content_key from alrawi_project_content  where project_id=?1" ,nativeQuery = true)
    String getProjectContentKey(Long projectId);
}
