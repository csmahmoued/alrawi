package eg.alrawi.alrawi_award.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RegisterRequest {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String city;
    private String government;
    private String nationalId;
    private String passPortNumber;
    private MultipartFile nationalIdFront;
    private MultipartFile nationalIdBack;
    private MultipartFile passPort;





}
