package dev.gagnon.Controller;

import dev.gagnon.DTO.ShopDto;
import dev.gagnon.DTO.request.AddTierRequest;
import dev.gagnon.DTO.request.PromoteShopRequest;
import dev.gagnon.DTO.request.UpdateAccountRequest;
import dev.gagnon.DTO.request.UpdatePhoneRequest;
import dev.gagnon.DTO.response.AccountDetailsResponse;
import dev.gagnon.DTO.response.ShopResponse;
import dev.gagnon.DTO.response.TierResponse;
import dev.gagnon.Exception.VendorBaseException;
import dev.gagnon.Service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute ShopDto dto,
            @RequestPart(value = "logoImage", required = false) MultipartFile logoImage) {
        try{
            var response = shopService.createShop(dto, logoImage);
            return new  ResponseEntity<>(response, CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("update-status")
    public ResponseEntity<?> updateStatus(@RequestParam String email) {
        try{
            var response = shopService.updateShopStatus(email);
            return new ResponseEntity<>(response,OK);
        }catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestParam Long shopId, @RequestBody UpdateAccountRequest request) {
        try{
            String response = shopService.updateShopAccount(shopId, request);
            return new ResponseEntity<>(response,OK);
        }catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/get-account")
    public ResponseEntity<?> getAccount(@RequestParam Long shopId) {
        try{
            AccountDetailsResponse response = shopService.getShopAccount(shopId);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/getbyEmail")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        try {
            var response = shopService.getShopByEmail(email);
            return ResponseEntity.ok(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping("/allCount")
    public ResponseEntity<?> getAllCount() {
        return ResponseEntity.ok(shopService.getAllShops().size());
    }

    @GetMapping("/allByMarket")
    public ResponseEntity<?> getAllByMarket(@RequestParam Long marketId) {
        List<ShopResponse> response = shopService.getMarketShops(marketId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allPlatinum")
    public ResponseEntity<?> getAllPlatinum() {
        List<ShopResponse> response = shopService.getPlatinumShops();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allByMarketCount")
    public ResponseEntity<?> getAllByMarketCount(@RequestParam Long marketId) {
        List<ShopResponse> response = shopService.getMarketShops(marketId);
        return ResponseEntity.ok(response.size());
    }

    @GetMapping("/allByMarketActiveShopsCount")
    public ResponseEntity<?> getAllByMarketActiveShopsCount(@RequestParam Long marketId) {
        return ResponseEntity.ok(shopService.getAllByMarketActiveShopsCount(marketId));
    }

    @GetMapping("/allActiveShopsCount")
    public ResponseEntity<?> getAllActiveShopsCount() {
        return ResponseEntity.ok(shopService.getAllByActiveShopsCount());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(shopService.getShopById(id));
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ShopDto dto) {
        try{
            return ResponseEntity.ok(shopService.updateShop(id, dto));
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/deactivateShop")
    public ResponseEntity<?> deactivateShop(@RequestParam Long shopId) {
        try{
            String response = shopService.deActivateShop(shopId);
            return ResponseEntity.ok(response);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/activateShop")
    public ResponseEntity<?> activateMarket(@RequestParam Long shopId) {
        try{
            String response = shopService.activateShop(shopId);
            return ResponseEntity.ok(response);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PostMapping("/add-tier")
    public ResponseEntity<?> addTier(@RequestBody AddTierRequest request) {
        try{
            String response = shopService.addTier(request);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/update-tier")
    public ResponseEntity<?> updateTier(@RequestParam Long tierId,@RequestBody AddTierRequest request) {
        try{
            String response = shopService.updateTier(tierId,request);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/all-tiers")
    public ResponseEntity<?> getAllTiers() {
        List<TierResponse>response = shopService.getAllTiers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tier/id")
    public ResponseEntity<?> getTier(@RequestParam Long tierId) {
        try{
            TierResponse response = shopService.getTier(tierId);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/tier/shop")
    public ResponseEntity<?> getShopTier(@RequestParam Long shopId) {
        try{
            TierResponse response = shopService.getShopTier(shopId);
            return new ResponseEntity<>(response,OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-tier")
    public ResponseEntity<?>deleteTier(@RequestParam Long tierId){
        try{
            String response = shopService.deleteTier(tierId);
            return new ResponseEntity<>(response, OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @PutMapping("/promote")
    public ResponseEntity<?> promoteShop(@RequestBody PromoteShopRequest request){
        try{
            String response = shopService.promoteShop(request);
            return ResponseEntity.ok(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/update-phone")
    public ResponseEntity<?>updatePhone(@RequestBody UpdatePhoneRequest request){
        try {
            String response = shopService.updatePhone(request);
            return ResponseEntity.ok(response);
        }
        catch (VendorBaseException e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/get-promoted")
    public ResponseEntity<?>getAllPromoted(){

        try {
            List<ShopResponse> shops = shopService.getAllPromoted();
            return ResponseEntity.ok(shops);
        }
        catch(VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchShopsWithProduct(@RequestParam String product, @RequestParam String market) {
        try {
            List<Map<String, Object>> results = shopService.searchShopsWithProduct(product, market);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching shops: " + e.getMessage());
        }
    }

    @GetMapping("/by-market")
    public ResponseEntity<?> getShopsByMarket(@RequestParam Long marketId) {
        try {
            List<ShopResponse> shops = shopService.getMarketShops(marketId);
            return ResponseEntity.ok(shops);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching shops for market: " + e.getMessage());
        }
    }

}
