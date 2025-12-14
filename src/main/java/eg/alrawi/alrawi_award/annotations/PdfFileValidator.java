package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PdfFileValidator implements ConstraintValidator<ValidPdf, MultipartFile> {

    private static final long MAX_SIZE = 15L * 1024 * 1024;
    private static final String PDF_MIME = "application/pdf";

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) {
            return true;
        }


        if (file.getSize() > MAX_SIZE) {
            return false;
        }

        String contentType = file.getContentType();
        if (!PDF_MIME.equalsIgnoreCase(contentType)) {
            return false;
        }

        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }
}
