package eg.alrawi.alrawi_award.controller;

import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.mail.Email;
import eg.alrawi.alrawi_award.mail.MailService;
import eg.alrawi.alrawi_award.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class TestController {

    private final FileService fileService;
    private final MailService  mailService;

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestBody List<String> emils) throws IOException {
        int count=0;
       for (String emil : emils) {
           Email email = getEmail(emil);
           mailService.sendMail(email);
           count++;
       }

        return new ResponseEntity<>("ok count "+count,HttpStatus.OK);
    }


    private Email getEmail(String userEmail) {
        Email email = new Email();
        email.setTo(userEmail);
        email.setSubject("AlRawiAwards");
        email.setFromEmail("alrawiawards@gmail.com");
        email.setFrom("alrawiawards@gmail.com");
        email.setTemplateName("email_template_alrawi2.ftl");

        return email;
    }


}
