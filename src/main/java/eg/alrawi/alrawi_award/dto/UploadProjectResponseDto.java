package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadProjectResponseDto {

    private String projectName;
    private String uploadUrl;
    private String projectId;
}
