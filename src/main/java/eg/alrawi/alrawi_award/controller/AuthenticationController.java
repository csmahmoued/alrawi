package eg.alrawi.alrawi_award.controller;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.OtpVerifyDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.SendOtpDto;
import eg.alrawi.alrawi_award.service.AuthenticationService;
import eg.alrawi.alrawi_award.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @ModelAttribute RegisterDto registerDto) {
        ApiResponseDto<?> apiResponseDto = authenticationService.registerUser(registerDto);
        if (apiResponseDto.isStatus())
            return new ResponseEntity<>(apiResponseDto, HttpStatus.CREATED);
      else
          return new ResponseEntity<>(apiResponseDto, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDto sendOtpDto) {
       ApiResponseDto<?> responseDto= otpService.sendOtp(sendOtpDto.getEmail());
       if (responseDto.isStatus())
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
       else
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyDto otpVerifyDto) {
        ApiResponseDto<?> responseDto=otpService.validateOtp(otpVerifyDto);
        if (responseDto.isStatus())
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
         else
             return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
    }


    @GetMapping("/hello")
    public ResponseEntity<?> healthCheck(){
        return new ResponseEntity<>("Hello Ok",HttpStatus.OK);
    }
}
