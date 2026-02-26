package eg.alrawi.alrawi_award.entity;

import eg.alrawi.alrawi_award.model.CategoryContentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@Setter
@Getter
public class AlrawiCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private CategoryContentType categoryContentType;

    private String color;

    private String imgUrl;

    @OneToMany(mappedBy = "alrawiCategory",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AlrawiProject> projects;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<CategoryDescription> categoryDescriptions;
}
