package eg.alrawi.alrawi_award.dto;


import eg.alrawi.alrawi_award.annotations.NationalId;
import eg.alrawi.alrawi_award.annotations.Passport;
import eg.alrawi.alrawi_award.annotations.ValidImage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Setter
@Getter
public class RegisterDto implements Serializable {

    private String password;
    @Email(message = "{email.invalid.message}")
    private String email;

    @Size(max=50,message = "{name.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{name.validation.message}")
    private String fullName;

    @Pattern(regexp = "^(?:\\+20|0)?1[0125][0-9]{8}$",message = "{invalid.mobile.message}")
    private String mobileNumber;

    @Size(max=20,message ="{city.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{city.validation.message}")
    private String city;

    @Size(max=20,message ="{city.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{city.validation.message}")
    private String government;

    @Pattern( regexp = "^\\d{14}$", message = "{national.invalid.length}")
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


    @Override
    public String toString() {
        return "RegisterDto{" +
                "password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", city='" + city + '\'' +
                ", government='" + government + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
}
