package eg.alrawi.alrawi_award.dto;
import eg.alrawi.alrawi_award.annotations.ValidImage;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class UpdateUserDto implements Serializable {


    @Size(max=20,message ="{city.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{city.validation.message}")
    private String city;

    @Size(max=20,message ="{city.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{city.validation.message}")
    private String government;


    @Size(max=50,message = "{name.max.valid.message}")
    @Pattern(regexp = "^[a-zA-Z\\u0621-\\u064A ]+$" ,message = "{name.validation.message}")
    private String fullName;

    @Email(message = "{email.invalid.message}")
    private String email;

    @Pattern(regexp = "^(\\+20)?1[0-9]{9}$" ,message = "{invalid.mobile.message}")
    private String mobileNumber;

    @ValidImage
    private MultipartFile profilePicture;


    @Override
    public String toString() {
        return "UpdateUserDto{" +
                "city='" + city + '\'' +
                ", government='" + government + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}
