package eg.alrawi.alrawi_award.dto;

import eg.alrawi.alrawi_award.annotations.ValidImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class FileDto {


    @ValidImage
    private MultipartFile file;
}
