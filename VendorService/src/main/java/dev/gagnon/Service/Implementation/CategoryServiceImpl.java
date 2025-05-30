package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.Category;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.CategoryRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        User creator = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getCreatedById()));

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .createdBy(creator)
                .build();

        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        User creator = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getCreatedById()));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedBy(creator);

        return mapToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdById(category.getCreatedBy().getId())
                .createdAt(category.getCreatedAt() != null
                        ? category.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .build();
    }
}
