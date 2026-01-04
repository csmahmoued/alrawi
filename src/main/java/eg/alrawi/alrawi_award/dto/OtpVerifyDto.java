package eg.alrawi.alrawi_award.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Setter
@Getter
public class OtpVerifyDto implements Serializable {

    @NotBlank(message = "OTP cannot be blank")
    @Pattern(regexp = "\\d{6}", message = "{invalid.otp.message}")
    private String otp;

    @Email(message = "{email.invalid.message}")
    private String email;


}
