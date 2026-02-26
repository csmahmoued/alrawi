package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table
public class AlrawiProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectTitle;

    @Column(length = 350)
    private String projectDescription;

   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
   @JoinColumn(name = "category_id")
   private AlrawiCategory alrawiCategory;


   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
   @JoinColumn(name = "user_id")
   private AlrawiUser alrawiUser;

   private String projectStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private Date dateCreated;

    @OneToMany(mappedBy = "alrawiProject",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private  List<AlrawiProjectContent>  alrawiProjectContent;


}
