package eg.alrawi.alrawi_award.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class ProjectContentDto {

    private Long categoryId;
    private String projectTitle;
    private String projectDescription;

    @Size(max = 10, message = "max upload images is 10")
    @Valid
    private List<FileDto> uploads;

    private MultipartFile file;

}
