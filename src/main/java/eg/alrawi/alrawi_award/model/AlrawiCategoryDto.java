package eg.alrawi.alrawi_award.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AlrawiCategoryDto {

    private String categoryContent;
    private String categoryCode;
    List<CategoryRequestBody> categoryDtoList;
}
