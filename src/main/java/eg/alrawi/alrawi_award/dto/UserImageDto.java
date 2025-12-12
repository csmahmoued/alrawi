package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UserImageDto {

    private MultipartFile nationalIdFront;
    private MultipartFile nationalIdBack;
    private MultipartFile passPort;
    private MultipartFile profileImage ;

}
