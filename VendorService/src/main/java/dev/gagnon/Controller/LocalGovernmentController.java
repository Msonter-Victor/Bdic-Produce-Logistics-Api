package dev.gagnon.Controller;

import dev.gagnon.DTO.LocalGovernmentDTO;
import dev.gagnon.Service.LocalGovernmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local-governments")
@RequiredArgsConstructor
public class LocalGovernmentController {

    private final LocalGovernmentService localGovernmentService;

    @PostMapping("/add")
    public ResponseEntity<LocalGovernmentDTO> create(@RequestBody LocalGovernmentDTO dto) {
        return ResponseEntity.ok(localGovernmentService.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LocalGovernmentDTO>> getAll() {
        return ResponseEntity.ok(localGovernmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalGovernmentDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(localGovernmentService.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LocalGovernmentDTO> update(@PathVariable Long id, @RequestBody LocalGovernmentDTO dto) {
        return ResponseEntity.ok(localGovernmentService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        localGovernmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
