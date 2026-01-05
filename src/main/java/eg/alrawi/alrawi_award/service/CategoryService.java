package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.CategoryDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.CategoryDescription;
import eg.alrawi.alrawi_award.model.AlrawiCategoryDto;
import eg.alrawi.alrawi_award.model.CategoryContentType;
import eg.alrawi.alrawi_award.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ApiResponseDto<List<CategoryDto>> getAllCategories(){

        try{

            String languageId = LocaleContextHolder.getLocale().getLanguage();
            List<AlrawiCategory>  categories = categoryRepository.findAllWithDescriptionByLanguage(languageId);

            if(categories.isEmpty())
                return ApiResponseDto.error(List.of("No Category Found "));

            List<CategoryDto> categoryDTOs= populateCategory(categories);

            return ApiResponseDto.success(categoryDTOs,"SUCCESS");
        }catch(Exception e){
            log.info("An error has occurred while trying to get the categories ",e);
            return ApiResponseDto.error(List.of("An error has been occurred , please try again "));

        }

    }

    private List<CategoryDto> populateCategory(List<AlrawiCategory> categories) {

        List<CategoryDto> categoryDTOs = new ArrayList<>();
        categories.forEach(category -> {
           category.getCategoryDescriptions().forEach(categoryDescription -> {
               CategoryDto categoryDto = new CategoryDto();
               categoryDto.setCategoryName(categoryDescription.getCategoryName());
               categoryDto.setCategoryDescription(categoryDescription.getDescription());
               categoryDto.setCategoryId(category.getCategoryId());
               categoryDto.setCategoryContentType(category.getCategoryContentType().toString());
               categoryDTOs.add(categoryDto);

           });

        });

        return categoryDTOs;
    }


    public void createCategory(AlrawiCategoryDto categoryDto) {
        try {
        AlrawiCategory alrawiCategory = new AlrawiCategory();
            List<CategoryDescription> categoryDescriptions = new ArrayList<>();

         categoryDto.getCategoryDtoList().forEach(alrawiCategoryDto -> {
            CategoryDescription  categoryDescription = new CategoryDescription();
            categoryDescription.setCategoryName(alrawiCategoryDto.getCategoryName());
            categoryDescription.setDescription(alrawiCategoryDto.getCategoryDescription());
            categoryDescription.setLanguageId(alrawiCategoryDto.getLanguageId());
            categoryDescription.setCategory(alrawiCategory);
            categoryDescriptions.add(categoryDescription);
        });

         CategoryContentType categoryContentType=getCategoryContentType(categoryDto);
         if(categoryContentType !=null) {
             alrawiCategory.setCategoryContentType(categoryContentType);
             alrawiCategory.setCategoryDescriptions(categoryDescriptions);
             alrawiCategory.setCategoryName(categoryDto.getCategoryCode());
             categoryRepository.save(alrawiCategory);
         }



        }catch(Exception e){
            log.info("An error has occurred while trying to create the category ",e);
        }

    }

    private CategoryContentType getCategoryContentType(AlrawiCategoryDto categoryDto) {

        return switch (categoryDto.getCategoryContent()) {
            case "video" -> CategoryContentType.VIDEO;
            case "audio" -> CategoryContentType.MP3;
            case "image" -> CategoryContentType.IMAGE;
            case "pdf" -> CategoryContentType.PDF;
            default -> null;
        };

    }
}
