package eg.alrawi.alrawi_award.dto;
import eg.alrawi.alrawi_award.annotations.ValidImage;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class UpdateUserDto implements Serializable {


    @Size( max = 50, message = "city must be between 2 and 50 characters")
    @Pattern(  regexp = "^(?=.*\\p{L})[\\p{L} ]+$", message = "city must contain letters only")
    private String city;

    @Size( max = 50, message = "government must be between 2 and 50 characters")
    @Pattern(  regexp = "^(?=.*\\p{L})[\\p{L} ]+$", message = "government must contain letters only")
    private String government;


    @Size( max = 100, message = "fullName must be between 2 and 100 characters")
    @Pattern(  regexp = "^(?=.*\\p{L})[\\p{L} ]+$", message = "fullName must contain letters only")
    private String fullName;

    @Size(max = 80)
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^(\\+20|0)?1[0125][0-9]{8}$", message = "invalid mobile number ")
    private String mobileNumber;

    @ValidImage
    private MultipartFile profilePicture;
}
