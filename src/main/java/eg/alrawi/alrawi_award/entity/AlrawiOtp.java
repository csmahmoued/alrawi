package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table
@Data
public class AlrawiOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime expiryTime;

    @CreationTimestamp
    @Column(updatable = false)
    private Date sentDate;

    private Integer otpCounter=0;

    private LocalDateTime verifiedDate;

    private boolean verified;


}
