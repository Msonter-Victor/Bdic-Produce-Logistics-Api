package dev.gagnon.Controller;

import dev.gagnon.DTO.CouncilWardDTO;
import dev.gagnon.Service.CouncilWardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/council-wards")
@RequiredArgsConstructor
public class CouncilWardController {

    private final CouncilWardService councilWardService;

    @PostMapping("add")
    public ResponseEntity<CouncilWardDTO> create(@RequestBody CouncilWardDTO dto) {
        return ResponseEntity.ok(councilWardService.create(dto));
    }

    @GetMapping("all")
    public ResponseEntity<List<CouncilWardDTO>> getAll() {
        return ResponseEntity.ok(councilWardService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouncilWardDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(councilWardService.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CouncilWardDTO> update(@PathVariable Long id, @RequestBody CouncilWardDTO dto) {
        return ResponseEntity.ok(councilWardService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        councilWardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
