package eg.alrawi.alrawi_award.controller;


import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.CategoryDto;
import eg.alrawi.alrawi_award.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category/")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(){
      ApiResponseDto<List<CategoryDto>> categoryResponse= categoryService.getAllCategories();
      if (categoryResponse.isStatus())
            return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
      else
          return new ResponseEntity<>(categoryResponse, HttpStatus.BAD_REQUEST);
    }

}
