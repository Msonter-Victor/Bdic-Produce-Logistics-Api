package dev.gagnon.Controller;

import dev.gagnon.DTO.MarketDto;
import dev.gagnon.Service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @PostMapping("/add")
    public ResponseEntity<MarketDto> createMarket(@RequestBody MarketDto marketDto) {
        MarketDto createdMarket = marketService.createMarket(marketDto);
        return ResponseEntity.ok(createdMarket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketDto> getMarketById(@PathVariable Long id) {
        MarketDto market = marketService.getMarketById(id);
        return ResponseEntity.ok(market);
    }

    @GetMapping("all")
    public ResponseEntity<List<MarketDto>> getAllMarkets() {
        List<MarketDto> markets = marketService.getAllMarkets();
        return ResponseEntity.ok(markets);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MarketDto> updateMarket(@PathVariable Long id, @RequestBody MarketDto marketDto) {
        MarketDto updatedMarket = marketService.updateMarket(id, marketDto);
        return ResponseEntity.ok(updatedMarket);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMarket(@PathVariable Long id) {
        marketService.deleteMarket(id);
        return ResponseEntity.noContent().build();
    }
}
