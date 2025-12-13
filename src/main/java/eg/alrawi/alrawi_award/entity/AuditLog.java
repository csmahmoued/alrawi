package eg.alrawi.alrawi_award.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String httpMethod;
    private String uri;
    private String username;
    private String roles;
    private String ipAddress;
    private Integer statusCode;
    private boolean success;
    private Long responseTime;
    private String transactionId;

    @CreationTimestamp
    private Instant createdAt;
}
