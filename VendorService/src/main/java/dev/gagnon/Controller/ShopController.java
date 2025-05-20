package dev.gagnon.Controller;

import dev.gagnon.DTO.ShopDto;
import dev.gagnon.Service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/add")
    public ResponseEntity<ShopDto> create(
            @RequestPart("dto") ShopDto dto,
            @RequestPart(value = "logoImage", required = false) MultipartFile logoImage
    ) {
        return ResponseEntity.ok(shopService.createShop(dto, logoImage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShopDto>> getAll() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ShopDto> update(@PathVariable Long id, @RequestBody ShopDto dto) {
        return ResponseEntity.ok(shopService.updateShop(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }
}
