package eg.alrawi.alrawi_award.controller;
import eg.alrawi.alrawi_award.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayInputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    @GetMapping("user/projects/v1/reports")
    public ResponseEntity<InputStreamResource> exportUsers() throws IOException {

        ByteArrayInputStream excelFile=reportService.exportUsers();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));

    }

    @GetMapping("user/projects/v1/users/reports")
    public ResponseEntity<InputStreamResource> exportUsersOnly() throws IOException {

        ByteArrayInputStream excelFile=reportService.exportUsersTest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));

    }

}
