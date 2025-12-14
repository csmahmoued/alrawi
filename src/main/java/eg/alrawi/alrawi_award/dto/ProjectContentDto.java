package eg.alrawi.alrawi_award.dto;

import eg.alrawi.alrawi_award.annotations.ValidImage;
import eg.alrawi.alrawi_award.annotations.ValidPdf;
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

    @ValidPdf
    private MultipartFile scriptFile;

    @Valid
    @Size(max = 10, message = "max upload images is 10")
    private List<@ValidImage MultipartFile> imgFile;

}
