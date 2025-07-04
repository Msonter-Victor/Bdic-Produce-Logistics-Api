package dev.gagnon.Service;
import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.DTO.SubCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);

    Object createSubCategory(SubCategoryRequest request);
}
