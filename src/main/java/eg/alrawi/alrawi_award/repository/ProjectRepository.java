package eg.alrawi.alrawi_award.repository;


import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<AlrawiProject,Long> {

    List<AlrawiProject> findByAlrawiUser(AlrawiUser alrawiUser);
}
