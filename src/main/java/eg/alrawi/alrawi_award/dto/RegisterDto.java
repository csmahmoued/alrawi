package eg.alrawi.alrawi_award.dto;


import eg.alrawi.alrawi_award.annotations.NationalId;
import eg.alrawi.alrawi_award.annotations.Passport;
import eg.alrawi.alrawi_award.annotations.ValidImage;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Setter
@Getter
public class RegisterDto implements Serializable {

    private String password;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String email;
    private String fullName;
    private String mobileNumber;
    private String city;
    private String government;

    @NationalId(message = "{national.invalid.format}")
    private String nationalId;

    @Passport
    private String passportNumber;

    @ValidImage
    private MultipartFile nationalImgFront;

    @ValidImage
    private MultipartFile nationalImgBack;

    @ValidImage
    private MultipartFile passportImg;

    private String dateOfBirth;


}
