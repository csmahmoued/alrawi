package eg.alrawi.alrawi_award.mapper;


import eg.alrawi.alrawi_award.dto.CategoryDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.CategoryDescription;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {

    AlrawiCategory mapCategory(CategoryDto categoryDto);

    CategoryDto mapCategory(AlrawiCategory alrawiCategory);

    List<CategoryDto> mapCategory(List<AlrawiCategory> alrawiCategories);



}
