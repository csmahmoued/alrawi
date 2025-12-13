package eg.alrawi.alrawi_award.utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NationalUtils {

    public static String extractGender(String nationalId) {
        int genderDigit = Character.getNumericValue(nationalId.charAt(12));
        return (genderDigit % 2 == 0) ? "Female" : "Male";
    }

    public static String extractBirthDateFormatted(String nationalId) {
        LocalDate birthDate = extractBirthDate(nationalId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d - MMMM - yyyy", Locale.ENGLISH);
        return birthDate.format(formatter);
    }

    public static LocalDate extractBirthDate(String nationalId) {

        char centuryDigit = nationalId.charAt(0);
        int yearPart = Integer.parseInt(nationalId.substring(1, 3));
        int month = Integer.parseInt(nationalId.substring(3, 5));
        int day = Integer.parseInt(nationalId.substring(5, 7));

        int fullYear = 0;
        if (centuryDigit == '2') {
            fullYear = 1900 + yearPart;
        } else if (centuryDigit == '3') {
            fullYear = 2000 + yearPart;
        }
        return LocalDate.of(fullYear, month, day);

    }


}
