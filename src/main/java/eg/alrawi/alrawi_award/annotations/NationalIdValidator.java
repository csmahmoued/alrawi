package eg.alrawi.alrawi_award.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Set;

public class NationalIdValidator implements ConstraintValidator<NationalId, String> {

    private static final Set<String> VALID_GOV_CODES = Set.of(
            "01","02","03","04","11","12","13","14","15","16","17","18","19",
            "21","22","23","24","25","26","27","28","29","31","32","33","34","35"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank())
            return true;

        if (!value.matches("\\d{14}"))
            return false;


        char firstDigit = value.charAt(0);

        if (firstDigit != '2' && firstDigit != '3') {
            return false;
        }


        String yearPart = value.substring(1, 3);
        String monthPart = value.substring(3, 5);
        String dayPart = value.substring(5, 7);

        int year = Integer.parseInt(yearPart);
        int month = Integer.parseInt(monthPart);
        int day = Integer.parseInt(dayPart);

        int fullYear = (firstDigit == '2' ? 1900 : 1999) + year;



        try {
            YearMonth ym = YearMonth.of(fullYear, month);
            if (day < 1 || day > ym.lengthOfMonth()) {
                return false;
            }
        } catch (DateTimeException e) {
            return false;
        }

        String govCode = value.substring(7, 9);

        return VALID_GOV_CODES.contains(govCode);
    }
}