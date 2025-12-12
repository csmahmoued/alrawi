package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.OtpVerifyDto;
import eg.alrawi.alrawi_award.entity.AlrawiOtp;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.mail.Email;
import eg.alrawi.alrawi_award.mail.MailService;
import eg.alrawi.alrawi_award.model.LoginResponse;
import eg.alrawi.alrawi_award.repository.OtpRepository;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final MailService mailService;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public ApiResponseDto<?> sendOtp(String userEmail) {

        try {
            AlrawiUser alrawiUser = userRepository.findByEmail(userEmail).orElse(null);
            if (alrawiUser == null)
                return ApiResponseDto.error(List.of("user not found"), "FAIL");

            String otp = generateOtp();
            Email email = getEmail(userEmail, otp);
            auditOtp(userEmail, otp);
            mailService.sendMail(email);
        } catch (Exception e) {
            log.info("An error occurred while sending an OTP ", e);
            return ApiResponseDto.error(List.of("An error occurred while sending an OTP"), "FAILED");
        }

        return ApiResponseDto.success("Successfully sent an OTP please check your mail NOTE: Can’t find your code? Be sure to check your spam/junk folder.", "SUCCESS");
    }


    private void auditOtp(String userEmail, String otp) {
        AlrawiOtp alrawiOtp = new AlrawiOtp();
        alrawiOtp.setOtp(otp);
        alrawiOtp.setOtpCounter(1);
        alrawiOtp.setExpiryTime(LocalDateTime.now().plusMinutes(2));
        alrawiOtp.setVerified(false);
        alrawiOtp.setEmail(userEmail);
        otpRepository.save(alrawiOtp);
    }

    private static String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    public boolean isOtpValid(OtpVerifyDto otpVerifyDto) {
        AlrawiOtp alrawiOtp = otpRepository.findByEmailAndOtp(otpVerifyDto.getEmail(), otpVerifyDto.getOtp()).orElse(null);
        if (alrawiOtp == null) {
            log.info("mail not found");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        log.info("time now is {}", now);
        log.info("time expiration is {}", alrawiOtp.getExpiryTime());
        boolean isWithinValidity = now.isBefore(alrawiOtp.getExpiryTime());

        if (!isWithinValidity) return false;

        alrawiOtp.setOtpCounter(alrawiOtp.getOtpCounter() + 1);
        alrawiOtp.setVerifiedDate(now);
        alrawiOtp.setVerified(true);
        otpRepository.save(alrawiOtp);
        return true;

    }

    public ApiResponseDto<?> validateOtp(OtpVerifyDto otpVerifyDto) {

        boolean valid = isOtpValid(otpVerifyDto);
        if (valid) {
            AlrawiUser alrawiUser = userRepository.findByEmail(otpVerifyDto.getEmail()).orElse(null);
            if (alrawiUser == null)
                return ApiResponseDto.error(List.of("FAIL"), "user not found");

            try {
                alrawiUser.setPassword(passwordEncoder.encode(otpVerifyDto.getOtp()));
                userRepository.save(alrawiUser);
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(otpVerifyDto.getEmail(), otpVerifyDto.getOtp()));
            } catch (Exception e) {
                log.info("An error occurred while validating an OTP ", e);
            }
            String token = jwtService.generateToken(alrawiUser);
            LoginResponse loginResponse = new LoginResponse(token);
            return ApiResponseDto.success(loginResponse, "OTP is valid ");
        } else
            return ApiResponseDto.error(List.of("Invalid OTP"), "FAIL");
    }


    private Email getEmail(String userEmail, String otp) {
        Email email = new Email();
        email.setTo(userEmail);
        email.setSubject("AlRawi");
        email.setFromEmail("mahmoud.eamohamed@outlook.com");
        email.setFrom("mahmoud.eamohamed@outlook.com");
        email.setTemplateName("email_template_alrawi.ftl");
        Map<String, String> model = new HashMap<>();
        model.put("otp",otp);
        email.setTemplateTokens(model);
        return email;
    }

}
