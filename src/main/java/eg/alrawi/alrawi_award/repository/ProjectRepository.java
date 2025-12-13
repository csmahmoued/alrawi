package eg.alrawi.alrawi_award.repository;


import eg.alrawi.alrawi_award.entity.AlrawiProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends JpaRepository<AlrawiProject,Long> {

}
