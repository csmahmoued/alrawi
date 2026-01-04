package eg.alrawi.alrawi_award.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class SendOtpDto implements Serializable {

    @Email(message = "{email.invalid.message}")
    private String email;


}
