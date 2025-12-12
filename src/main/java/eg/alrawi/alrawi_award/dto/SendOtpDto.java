package eg.alrawi.alrawi_award.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpDto {

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",message = "not valid mail")
    private String email;
}
