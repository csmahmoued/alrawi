package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity
@Data
public class FileStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String status ;

    private String  duration;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
}
