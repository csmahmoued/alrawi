package eg.alrawi.alrawi_award.controller;


import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.ProjectContentDto;
import eg.alrawi.alrawi_award.service.ProjectContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class ProjectContentController {


    private final ProjectContentService projectContentService;

    @PostMapping("/project")
    public ResponseEntity<?> createProject(@Valid @ModelAttribute ProjectContentDto alrawiProject){
       ApiResponseDto<?> apiResponseDto= projectContentService.uploadProject(alrawiProject);
       if (apiResponseDto.isStatus())
           return new ResponseEntity<>(apiResponseDto, HttpStatus.CREATED);

       else
           return new ResponseEntity<>(apiResponseDto, HttpStatus.BAD_REQUEST);
    }

}
