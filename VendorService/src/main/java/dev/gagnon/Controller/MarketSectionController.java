package dev.gagnon.Controller;

import dev.gagnon.DTO.MarketSectionDto;
import dev.gagnon.DTO.request.AddSectionRequest;
import dev.gagnon.DTO.request.UpdateSectionRequest;
import dev.gagnon.DTO.response.MarketSectionResponse;
import dev.gagnon.Exception.VendorBaseException;
import dev.gagnon.Service.MarketSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/market-sections")
@RequiredArgsConstructor
public class MarketSectionController {
    private final MarketSectionService sectionService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody AddSectionRequest dto) {
        return ResponseEntity.ok(sectionService.createSection(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketSectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MarketSectionResponse>> getAll() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @GetMapping("/allCount")
    public ResponseEntity<?> getAllCount() {
        return ResponseEntity.ok(sectionService.getAllSectionsCount());
    }

    @GetMapping("/allByMarket")
    public ResponseEntity<?> getAllByMarket(@RequestParam Long marketId) {
        List<MarketSectionResponse> response = sectionService.getAllSectionsForMarket(marketId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allByMarketCount")
    public ResponseEntity<?> getAllByMarketCount(@RequestParam Long marketId) {
        List<MarketSectionResponse> response = sectionService.getAllSectionsForMarket(marketId);
        return ResponseEntity.ok(response.size());
    }

    @PutMapping("/activate-line")
    public ResponseEntity<?> activateLine(@RequestParam Long id) {
        try{
            String response = sectionService.activateLine(id);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @PutMapping("/deActivate-line")
    public ResponseEntity<?> deActivateLine(@RequestParam Long id) {
        try{
            String response = sectionService.deActivateLine(id);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateSectionRequest dto) {
        return ResponseEntity.ok(sectionService.updateSection(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}
