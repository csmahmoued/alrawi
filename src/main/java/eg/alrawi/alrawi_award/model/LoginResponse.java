package eg.alrawi.alrawi_award.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {

    private String toke;

    public LoginResponse(){}

    public LoginResponse(String toke) {
        this.toke = toke;
    }

}
