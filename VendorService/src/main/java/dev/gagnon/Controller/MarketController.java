package dev.gagnon.Controller;

import dev.gagnon.DTO.MarketDto;
import dev.gagnon.DTO.ShopDto;
import dev.gagnon.DTO.request.AddMarketRequest;
import dev.gagnon.DTO.request.UpdateMarketRequest;
import dev.gagnon.DTO.response.MarketResponse;
import dev.gagnon.Exception.VendorBaseException;
import dev.gagnon.Model.Market;
import dev.gagnon.Service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @PostMapping("/add")
    public ResponseEntity<?> createMarket(@RequestBody AddMarketRequest request) {
        MarketResponse createdMarket = marketService.createMarket(request);
        return ResponseEntity.ok(createdMarket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMarketById(@PathVariable Long id) {
        MarketResponse market = marketService.getMarketById(id);
        return ResponseEntity.ok(market);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMarkets() {
        List<MarketResponse> markets = marketService.getAllMarkets();
        return ResponseEntity.ok(markets);
    }

    @GetMapping("/allCount")
    public ResponseEntity<?>getAllMarketsCount() {
        Long marketCount  = (long) marketService.getAllMarkets().size();
        return ResponseEntity.ok(marketCount);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMarket(@PathVariable Long id, @RequestBody UpdateMarketRequest request) {
        MarketResponse updatedMarket = marketService.updateMarket(id, request);
        return ResponseEntity.ok(updatedMarket);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAllMarkets() {
        List<Market> markets = marketService.saveAll();
        return ResponseEntity.ok(markets);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMarket(@PathVariable Long id) {
        marketService.deleteMarket(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/deActivateMarket")
    public ResponseEntity<?> deActivateMarket(@RequestParam Long id) {
        try{
            String response = marketService.deActivateMarket(id);
            return ResponseEntity.ok(response);
        }
        catch(VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }

    }

    @PutMapping("/activateMarket")
    public ResponseEntity<?> activateMarket(@RequestParam Long id) {
        try{
            String response = marketService.activateMarket(id);
            return ResponseEntity.ok(response);
        }
        catch(VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/getActiveMarketsCount")
    public ResponseEntity<?> getActiveMarketsCount(){
        Long activeMarkets = marketService.getActiveMarkets();
        return ResponseEntity.ok(activeMarkets);
    }

}
