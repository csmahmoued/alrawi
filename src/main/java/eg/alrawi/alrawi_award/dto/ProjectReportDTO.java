package eg.alrawi.alrawi_award.dto;
import java.util.Date;


public interface ProjectReportDTO {

    Long getId();
    String getName();
    String getEmail();
    String getPhone();
    String getNationalId();
    String getPassport();
    Date getDateCreated();
    String getCategory();
    String getProjectContent();
    String getStatus();
}
