package eg.alrawi.alrawi_award.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequestBody {

    private String categoryName;
    private String categoryDescription;
    private String languageId;
}
