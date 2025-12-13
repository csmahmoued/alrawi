package eg.alrawi.alrawi_award.controller;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute RegisterDto  registerDto){
        ApiResponseDto<?> apiResponseDto= authenticationService.updateUser(registerDto);
        if (apiResponseDto.isStatus())
            return new ResponseEntity<>(apiResponseDto, HttpStatus.OK);
        else
            return new ResponseEntity<>(apiResponseDto, HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUser(){
        ApiResponseDto<?> response=authenticationService.getUserProfile();
        if (response.isStatus())
            return new ResponseEntity<>(response, HttpStatus.OK);
        else
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
