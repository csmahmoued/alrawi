package eg.alrawi.alrawi_award.service;
import eg.alrawi.alrawi_award.entity.*;
import eg.alrawi.alrawi_award.repository.CategoryRepository;
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

    private final CategoryRepository categoryRepository;

    public List<AlrawiCategory> getAlrawiCategory() {
        return categoryRepository.findAll();
    }

    public ByteArrayInputStream exportCategory() throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Users");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"category", "CategoryDesc"};
            for (int i = 0; i < headers.length; i++)
                headerRow.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;

            List<AlrawiCategory> alrawiCategoryList=getAlrawiCategory();
            fillSheetCategoryDescription(alrawiCategoryList,sheet,rowIdx);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static  void fillSheetCategoryDescription(List<AlrawiCategory> categoryList,Sheet sheet, int rowIdx) {
        for (AlrawiCategory category : categoryList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("insert into alrawi_category(category_id,category_name,category_content_type) values ('"+category.getCategoryId()+"','"+category.getCategoryName()+"','"+category.getCategoryContentType()+"');");
            StringBuilder des= new StringBuilder();
         for (CategoryDescription categoryDescription : category.getCategoryDescriptions()) {
                des.append("insert into category_description (id,category_name,description,language_id) values ('").append(categoryDescription.getId()).append("' , '").append(categoryDescription.getCategoryName()).append("' ,  '").append(categoryDescription.getDescription()).append("','").append(categoryDescription.getLanguageId()).append("' , '").append(categoryDescription.getCategory().getCategoryId()).append("' ) ; \n");

         }
            row.createCell(1).setCellValue(des.toString());
         }

    }


}
