package eg.alrawi.alrawi_award.repository;

import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public  interface CategoryRepository extends JpaRepository<AlrawiCategory,Long> {

    @Query(" SELECT DISTINCT c FROM AlrawiCategory c JOIN FETCH c.categoryDescriptions d WHERE d.languageId = :languageId")
    List<AlrawiCategory> findAllWithDescriptionByLanguage(@Param("languageId") String languageId);
}
