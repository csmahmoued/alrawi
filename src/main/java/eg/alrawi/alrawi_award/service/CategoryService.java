package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.CategoryDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.mapper.CategoryMapper;
import eg.alrawi.alrawi_award.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public ApiResponseDto<List<CategoryDto>> getAllCategories(){

        try{
            List<AlrawiCategory>  categories = categoryRepository.findAll();
           if(categories.isEmpty())
                return ApiResponseDto.error(List.of("No Category Found "));

            List<CategoryDto> categoryDTOs= categoryMapper.mapCategory(categories);

            return ApiResponseDto.success(categoryDTOs,"SUCCESS");
        }catch(Exception e){
            log.info("An error has occurred while trying to get the categories ",e);
            return ApiResponseDto.error(List.of("An error has been occurred , please try again "));

        }

    }


}
