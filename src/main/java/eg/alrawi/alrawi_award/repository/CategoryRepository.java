package eg.alrawi.alrawi_award.repository;

import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface CategoryRepository extends JpaRepository<AlrawiCategory,Long> {
}
