package eg.alrawi.alrawi_award.entity;

import eg.alrawi.alrawi_award.model.ImageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageKey;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AlrawiUser alrawiUser;

    public UserImage() {}

    public UserImage(String imageKey, ImageType imageType) {
        this.imageKey = imageKey;
        this.imageType = imageType;
    }
}

