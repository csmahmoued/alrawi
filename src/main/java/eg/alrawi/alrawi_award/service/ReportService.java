package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ProjectReportDTO;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.repository.ProjectRepository;
import eg.alrawi.alrawi_award.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {


   private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ByteArrayInputStream exportUsers() throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Users");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Email", "Phone", "NationalId","Passport","DateCreated","Category",
                    "ProjectContent","Status"};

            for (int i = 0; i < headers.length; i++)
                headerRow.createCell(i).setCellValue(headers[i]);


            int rowIdx = 1;

            List<ProjectReportDTO> projectReportDTOS= projectRepository.getProjectReport();
            fillSheetData(projectReportDTOS, sheet, rowIdx);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


    public ByteArrayInputStream exportUsersTest() throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("All_Users");
            Row headerRow = sheet.createRow(0);
            String[] header = {"ID", "Name", "Email", "Phone", "NationalId","Passport","DateCreated"};

            for (int i = 0; i < header.length; i++)
                headerRow.createCell(i).setCellValue(header[i]);


            int rowIdx = 1;

            List<AlrawiUser> alrawiUserList= userRepository.findAll();
            fillSheetDataUsers(alrawiUserList, sheet, rowIdx);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


    private static void fillSheetData(List<ProjectReportDTO> projectReportDTOS, Sheet sheet, int rowIdx) {
        for (ProjectReportDTO projectReportDTO : projectReportDTOS) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(projectReportDTO.getId());
            row.createCell(1).setCellValue(projectReportDTO.getName());
            row.createCell(2).setCellValue(projectReportDTO.getEmail());
            row.createCell(3).setCellValue(projectReportDTO.getPhone());
            row.createCell(4).setCellValue(projectReportDTO.getNationalId());
            row.createCell(5).setCellValue(projectReportDTO.getPassport());
            row.createCell(6).setCellValue(projectReportDTO.getDateCreated().toString());
            row.createCell(7).setCellValue(projectReportDTO.getCategory());
            row.createCell(8).setCellValue(projectReportDTO.getProjectContent());

        }
    }

    private static void fillSheetDataUsers( List<AlrawiUser>  alrawiUserList, Sheet sheet, int rowIdx) {
        for (AlrawiUser alrawiUser : alrawiUserList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(alrawiUser.getUserId());
            row.createCell(1).setCellValue(alrawiUser.getFullName());
            row.createCell(2).setCellValue(alrawiUser.getEmail());
            row.createCell(3).setCellValue(alrawiUser.getMobileNumber());
            row.createCell(4).setCellValue(alrawiUser.getNationalId());
            row.createCell(5).setCellValue(alrawiUser.getPassportNumber());
            row.createCell(6).setCellValue(alrawiUser.getDateCreated().toString());
        }
    }


}
