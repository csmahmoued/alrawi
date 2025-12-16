package eg.alrawi.alrawi_award.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Setter
@Getter
public class CategoryDto implements Serializable {

    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryContentType;

}
