package eg.alrawi.alrawi_award.dto;


import eg.alrawi.alrawi_award.annotations.NationalId;
import eg.alrawi.alrawi_award.annotations.Passport;
import eg.alrawi.alrawi_award.annotations.ValidImage;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RegisterDto {

    private String password;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String email;
    private String fullName;
    private String mobileNumber;
    private String city;
    private String government;

    @NationalId(message = "national id is not valid ")
    private String nationalId;

    @Passport
    private String passportNumber;

    @ValidImage(maxSize = 5 * 1024 * 1024, allowedTypes = { "image/jpeg", "image/png" ,"image/jpg" }, message = "File must be a valid image (JPEG/PNG) under 5 MB")
    private MultipartFile nationalImgFront;

    @ValidImage(maxSize = 5 * 1024 * 1024, allowedTypes = { "image/jpeg", "image/png" ,"image/jpg" }, message = "File must be a valid image (JPEG/PNG) under 5 MB")
    private MultipartFile nationalImgBack;

    @ValidImage(maxSize = 5 * 1024 * 1024, allowedTypes = { "image/jpeg", "image/png" ,"image/jpg" }, message = "File must be a valid image (JPEG/PNG) under 5 MB")
    private MultipartFile passportImg;

}
