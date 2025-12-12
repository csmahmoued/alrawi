package eg.alrawi.alrawi_award.utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class NationalUtils {


    public static String extractGovernorate(String nationalId) {
        String govCode = nationalId.substring(7, 9);
        return GOV_CODES.getOrDefault(govCode, "Unknown Governorate");
    }

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

    private static final Map<String, String> GOV_CODES = Map.ofEntries(
            Map.entry("01", "Cairo"),
            Map.entry("02", "Alexandria"),
            Map.entry("03", "Port Said"),
            Map.entry("04", "Suez"),
            Map.entry("11", "Damietta"),
            Map.entry("12", "Dakahlia"),
            Map.entry("13", "Sharkia"),
            Map.entry("14", "Qalyubia"),
            Map.entry("15", "Kafr El Sheikh"),
            Map.entry("16", "Gharbia"),
            Map.entry("17", "Monufia"),
            Map.entry("18", "Beheira"),
            Map.entry("19", "Ismailia"),
            Map.entry("21", "Giza"),
            Map.entry("22", "Beni Suef"),
            Map.entry("23", "Fayoum"),
            Map.entry("24", "Minya"),
            Map.entry("25", "Assiut"),
            Map.entry("26", "Sohag"),
            Map.entry("27", "Qena"),
            Map.entry("28", "Aswan"),
            Map.entry("29", "Luxor"),
            Map.entry("31", "Red Sea"),
            Map.entry("32", "New Valley"),
            Map.entry("33", "Matrouh"),
            Map.entry("34", "North Sinai"),
            Map.entry("35", "South Sinai")
    );
}
