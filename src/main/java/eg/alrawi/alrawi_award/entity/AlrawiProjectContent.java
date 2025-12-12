package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Data;

@Table
@Entity
@Data
public class AlrawiProjectContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentKey;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private AlrawiProject  alrawiProject;



}
