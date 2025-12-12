package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class ProjectContentDto {

    private Long categoryId;
    private String projectTitle;
    private String projectDescription;

    private MultipartFile file;

}
