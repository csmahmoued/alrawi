package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserResponseDto implements Serializable {

    private String email;
    private String nationalId;
    private String passportNumber;
    private String fullName;
    private String city;
    private String government;
    private String dateOfBirth;
}
