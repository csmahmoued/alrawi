package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table
@Data
public class AlrawiCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false)
    private String categoryName;

    private String categoryDescription;

    @OneToMany(mappedBy = "alrawiCategory",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AlrawiProject> projects;

}
