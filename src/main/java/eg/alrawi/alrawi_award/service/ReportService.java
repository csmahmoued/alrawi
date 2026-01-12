package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {


    private final UserRepository userRepository;
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Tutorials";


    public void test(){

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        String[] headers = {"ID", "Name", "Email", "Phone", "NationalId","Passport","DateCreated","Category",
            "ProjectContent","Status"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

    }




}
