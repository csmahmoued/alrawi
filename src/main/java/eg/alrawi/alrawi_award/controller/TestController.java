package eg.alrawi.alrawi_award.controller;

import eg.alrawi.alrawi_award.entity.AlrawiCategory;
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

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class TestController {

    private final FileService fileService;

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestBody AlrawiCategory alrawiCategory) throws IOException {
      //  String imageBytes= fileService.downloadFileAsBase64("30108220100946/test1.jpg");
     //   BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        return new ResponseEntity<>(LocaleContextHolder.getLocale().getLanguage(),HttpStatus.OK);
    }



}
