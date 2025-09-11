package dev.gagnon.Controller;
import dev.gagnon.DTO.CategoryDto;
import dev.gagnon.DTO.SubCategoryRequest;
import dev.gagnon.DTO.response.CategoryResponse;
import dev.gagnon.DTO.response.SubCategoryResponse;
import dev.gagnon.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody CategoryDto dto) {
        try{
            return ResponseEntity.ok(categoryService.createCategory(dto));
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/getAllSub")
    public ResponseEntity<?> getAllSub() {
        return ResponseEntity.ok(categoryService.getAllSubCategories());
    }

    @GetMapping("/getAllCategorySub")
    public ResponseEntity<?> getAllCategorySub(@RequestParam String categoryName) {
        return ResponseEntity.ok(categoryService.getAllSubCategorySub(categoryName));
    }

    @PostMapping("/add-sub")
    public ResponseEntity<?> addSubCategory(SubCategoryRequest request){
        try {
            SubCategoryResponse response = categoryService.createSubCategory(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/getBy/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
