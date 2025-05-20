package dev.gagnon.Controller;

import dev.gagnon.DTO.MarketSectionDto;
import dev.gagnon.Service.MarketSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market-sections")
@RequiredArgsConstructor
public class MarketSectionController {
    private final MarketSectionService sectionService;

    @PostMapping("/add")
    public ResponseEntity<MarketSectionDto> create(@RequestBody MarketSectionDto dto) {
        return ResponseEntity.ok(sectionService.createSection(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketSectionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MarketSectionDto>> getAll() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MarketSectionDto> update(@PathVariable Long id, @RequestBody MarketSectionDto dto) {
        return ResponseEntity.ok(sectionService.updateSection(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}
