package eg.alrawi.alrawi_award.repository;


import eg.alrawi.alrawi_award.dto.ProjectReportDTO;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<AlrawiProject,Long> {

    @Query("""
        SELECT 
            u.userId AS id,
            u.fullName AS name,
            u.email AS email,
            u.mobileNumber AS phone,
            u.nationalId AS nationalId,
            u.passportNumber AS passport,
            u.dateCreated AS dateCreated,
            c.categoryName AS category,
            pc.contentKey AS projectContent,
            p.projectStatus AS status
        FROM AlrawiUser u
        JOIN u.projects p
        JOIN p.alrawiCategory c
        LEFT JOIN p.alrawiProjectContent pc
""")
    List<ProjectReportDTO> getProjectReport();



}
