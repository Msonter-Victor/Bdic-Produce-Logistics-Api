package dev.gagnon.Controller;
import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.Service.CategoryService;
import dev.gagnon.Util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

//    @PostMapping("/add")
//    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
//        return ResponseEntity.ok(categoryService.createCategory(dto));
//    }

@PostMapping("/add")
public ResponseEntity<CategoryDto> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody CategoryDto dto) {
    dto.setCreatedById(userDetails.getUser().getId());
    return ResponseEntity.ok(categoryService.createCategory(dto));
}

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
