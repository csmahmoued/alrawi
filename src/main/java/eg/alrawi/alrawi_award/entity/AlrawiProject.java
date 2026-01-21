package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table
public class AlrawiProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectTitle;

    @Column(length = 350)
    private String projectDescription;

   @ManyToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "category_id")
   private AlrawiCategory alrawiCategory;

   @ManyToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "user_id")
   private AlrawiUser alrawiUser;

   private String projectStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private Date dateCreated;

    @OneToMany(mappedBy = "alrawiProject",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private  List<AlrawiProjectContent>  alrawiProjectContent;


}
