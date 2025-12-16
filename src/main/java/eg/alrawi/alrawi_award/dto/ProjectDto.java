package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Setter
@Getter
public class ProjectDto implements Serializable {

    private String projectTitle;
    private String projectDescription;
    private String categoryName;
    private String projectStatus;
}
