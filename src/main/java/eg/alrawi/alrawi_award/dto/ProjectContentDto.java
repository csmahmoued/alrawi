package eg.alrawi.alrawi_award.dto;

import eg.alrawi.alrawi_award.annotations.ValidImage;
import eg.alrawi.alrawi_award.annotations.ValidPdf;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ProjectContentDto implements Serializable {

    private Long categoryId;

    @Size(min = 3, max = 30 , message = "{invalid.project.title.size}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{project.title.validation.message}")
    private String projectTitle;

    @Size(min = 1, max = 300 , message = "{invalid.project.description.size}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{project.description.validation.message}")
    private String projectDescription;

    @ValidPdf
    private MultipartFile scriptFile;

    @Valid
    @Size(max = 10, message = "{max.images.upload}")
    private List<@ValidImage MultipartFile> imgFile;


    @Override
    public String toString() {
        return "ProjectContentDto{" +
                "projectDescription='" + projectDescription + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
