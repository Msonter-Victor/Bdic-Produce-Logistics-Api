package dev.gagnon.Service;

import dev.gagnon.DTO.ShopDto;
import dev.gagnon.DTO.request.AddTierRequest;
import dev.gagnon.DTO.request.PromoteShopRequest;
import dev.gagnon.DTO.request.UpdateAccountRequest;
import dev.gagnon.DTO.request.UpdatePhoneRequest;
import dev.gagnon.DTO.response.AccountDetailsResponse;
import dev.gagnon.DTO.response.ShopResponse;
import dev.gagnon.DTO.response.TierResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ShopService {

    ShopResponse createShop(ShopDto dto, MultipartFile logoImage);

    ShopResponse getShopById(Long id);
    List<ShopResponse> getAllShops();
    ShopResponse updateShop(Long id, ShopDto dto);
    void deleteShop(Long id);
    ShopResponse getShopByEmail(String email);
    String updateShopStatus(String email);
    List<ShopResponse> getMarketShops(Long marketId);
    Long getAllByMarketActiveShopsCount(Long marketId);
    String deActivateShop(Long shopId);
    String promoteShop(PromoteShopRequest request);

    String updatePhone(UpdatePhoneRequest request);

    AccountDetailsResponse getShopAccount(Long shopId);

    String updateShopAccount(Long shopId, UpdateAccountRequest request);

    String addTier(AddTierRequest request);

    String updateTier(Long id,AddTierRequest request);

    List<TierResponse> getAllTiers();

    TierResponse getTier(Long tierId);

    String deleteTier(Long tierId);

    TierResponse getShopTier(Long shopId);

    List<ShopResponse> getAllPromoted();

    Long getAllByActiveShopsCount();

    List<Map<String, Object>> searchShopsWithProduct(String product, String market);

    List<ShopResponse> getPlatinumShops();

    String activateShop(Long shopId);
}