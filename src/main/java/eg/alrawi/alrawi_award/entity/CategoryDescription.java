package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
public class CategoryDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String categoryName;
    private String description;
    private String languageId;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlrawiCategory category;


}
