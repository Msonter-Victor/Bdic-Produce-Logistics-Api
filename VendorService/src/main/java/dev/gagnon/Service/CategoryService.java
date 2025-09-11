package dev.gagnon.Service;
import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.DTO.SubCategoryRequest;
import dev.gagnon.DTO.response.CategoryResponse;
import dev.gagnon.DTO.response.SubCategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryDto dto);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);
    SubCategoryResponse createSubCategory(SubCategoryRequest request);

    List<CategoryResponse> getAllSubCategories();

    List<CategoryResponse> getAllSubCategorySub(String name);
}
