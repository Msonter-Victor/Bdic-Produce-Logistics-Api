package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.DTO.SubCategoryRequest;
import dev.gagnon.DTO.response.CategoryResponse;
import dev.gagnon.DTO.response.SubCategoryResponse;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.Category;
import dev.gagnon.Model.SubCategory;
import dev.gagnon.Repository.CategoryRepository;
import dev.gagnon.Repository.SubCategoryRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryResponse createCategory(CategoryDto dto) {
                Category category = Category.builder()
                .name(dto.getName())
                .build();

        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToDto(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        category.setName(dto.getName());
        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public SubCategoryResponse createSubCategory(SubCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).get();
        SubCategory subCategory = new SubCategory();
        subCategory.setName(request.getName());
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);
        SubCategoryResponse response  = new SubCategoryResponse();
        response.setId(subCategory.getId());
        response.setName(request.getName());
        return response;
    }

    @Override
    public List<CategoryResponse> getAllSubCategories() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories
                .stream().map(this::mapToSubDto)
                .toList();
    }

    @Override
    public List<CategoryResponse> getAllSubCategorySub(String categoryName) {
        List<SubCategory> subCategories = subCategoryRepository.findAllByCategoryName(categoryName);
        return subCategories
                .stream().map(this::mapToSubDto)
                .toList();
    }

    private CategoryResponse mapToDto(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
    private CategoryResponse mapToSubDto(SubCategory category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
