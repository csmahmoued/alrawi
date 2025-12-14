package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {

    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryContentType;

}
