package dev.gagnon.Service.Implementation;

import com.cloudinary.Cloudinary;
import dev.gagnon.DTO.ShopDto;
import dev.gagnon.DTO.request.AddTierRequest;
import dev.gagnon.DTO.request.PromoteShopRequest;
import dev.gagnon.DTO.request.UpdateAccountRequest;
import dev.gagnon.DTO.request.UpdatePhoneRequest;
import dev.gagnon.DTO.response.AccountDetailsResponse;
import dev.gagnon.DTO.response.ShopResponse;
import dev.gagnon.DTO.response.TierResponse;
import dev.gagnon.Exception.BusinessException;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Exception.ShopExistsException;
import dev.gagnon.Model.*;
import dev.gagnon.Model.constants.PromotedStatus;
import dev.gagnon.Repository.*;
import dev.gagnon.Service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.gagnon.Model.constants.PromotedStatus.PROMOTED;
import static dev.gagnon.Model.constants.ShopStatus.*;
import static dev.gagnon.Util.ServiceUtils.getMediaUrl;
import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final ProductRepository productRepository;
    private final TierRepository tierRepository;

    @Override
    public ShopResponse createShop(ShopDto dto, MultipartFile logoImage) {
        User vendor = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("user not found with " + dto.getEmail()));
        boolean existsByEmail = shopRepository.existsByEmail(dto.getEmail());
        if (existsByEmail) throw new ShopExistsException("shop exists for this user");

        String logoUrl = null;
        if (logoImage != null && !logoImage.isEmpty()) {
            logoUrl = getMediaUrl(logoImage, cloudinary.uploader());
        }

        Market market = marketRepository.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("market not found"));

        MarketSection marketSection = marketSectionRepository.findByName(dto.getMarketSectionId());
        if (marketSection == null) {
            marketSection = new MarketSection();
            marketSection.setName(dto.getMarketSectionId());
            marketSection.setMarket(market);
            marketSectionRepository.save(marketSection);
        }
        Shop shop = Shop.builder()
                .status(NOT_VERIFIED)
                .user(vendor)
                .totalPayoutAmount(BigDecimal.valueOf(0))
                .marketSection(marketSection)
                .phone(dto.getPhone())
                .name(dto.getName())
                .promotedStatus(PromotedStatus.NOT_PROMOTED)
                .address(dto.getAddress())
                .bankCode(dto.getCode())
                .logoImage(logoUrl != null ? logoUrl : "")                .shopNumber(dto.getShopNumber())
                .homeAddress(dto.getHomeAddress())
                .streetName(dto.getStreetName())
                .cacNumber(dto.getCacNumber())
                .featuredNumber(0L)
                .floatedNumber(0L)
                .promotedNumber(0L)
                .taxIdNumber(dto.getTaxIdNumber())
                .nin(dto.getNin())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .market(market)
                .build();
        return mapToDto(shopRepository.save(shop));
    }

    @Override
    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));
        return mapToDto(shop);
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShopResponse updateShop(Long id, ShopDto dto) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));
        Market market = marketRepository.findById(dto.getMarketId()).orElseThrow(()->new ResourceNotFoundException("market not found"));
        MarketSection marketSection = marketSectionRepository.findByName(dto.getMarketSectionId());
        if (marketSection == null)
            marketSection = new MarketSection();
        marketSection.setMarket(market);
        marketSectionRepository.save(marketSection);
        shop.setName(dto.getName());
        shop.setPhone(dto.getPhone());
        shop.setAddress(dto.getAddress());
        shop.setShopNumber(dto.getShopNumber());
        shop.setHomeAddress(dto.getHomeAddress());
        shop.setStreetName(dto.getStreetName());
        shop.setCacNumber(dto.getCacNumber());
        shop.setTaxIdNumber(dto.getTaxIdNumber());
        shop.setNin(dto.getNin());
        shop.setBankCode(dto.getCode());
        shop.setBankName(dto.getBankName());
        shop.setAccountNumber(dto.getAccountNumber());
        shop.setMarket(market);
        shop.setMarket(marketRepository.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Market not found with ID: " + dto.getMarketId())));
        shop.setUser(userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getEmail())));
        return mapToDto(shopRepository.save(shop));
    }

    @Override
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));
        List<Product> list = productRepository.findAllByShop(shop.getId());
        productRepository.deleteAll(list);
        shopRepository.delete(shop);
    }

    @Override
    public ShopResponse getShopByEmail(String email) {
        Shop shop = shopRepository.getShopsByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("shop does not exists for this user"));
        if(shop.getPromotedStatus()==null)shop.setPromotedStatus(PromotedStatus.NOT_PROMOTED);
        if(shop.getTotalPayoutAmount()==null)shop.setTotalPayoutAmount(BigDecimal.ZERO);
        shopRepository.save(shop);
        ShopResponse response = mapToDto(shop);
        if(shop.getShopTier()!=null)response.setPromotedTierId(shop.getShopTier().getId());
        return response;
    }

    @Override
    public String updateShopStatus(String email) {
        Shop shop = shopRepository.getShopsByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("shop does not exists for this user"));
        shop.setStatus(VERIFIED);
        shopRepository.save(shop);
        return "status updated";
    }

    @Override
    public List<ShopResponse> getMarketShops(Long marketId) {
        List<Shop> shops = shopRepository.findAllByMarketId(marketId);
        if(shops.isEmpty())return List.of();
        return shops.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getAllByMarketActiveShopsCount(Long marketId) {
        List<Shop> shops = shopRepository.findAllByMarketId(marketId);
        List<Shop> activeShops = shops.stream()
                .filter(shop -> shop.getStatus().equals(VERIFIED))
                .toList();
        return (long) activeShops.size();
    }

    @Override
    public String deActivateShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        shop.setStatus(NOT_VERIFIED);
        shopRepository.save(shop);
        return "shop deactivated";
    }

    @Override
    public String promoteShop(PromoteShopRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
//        if(shop.getPromotedStatus()== PROMOTED)
//            throw new BusinessException("Promotion still active,wait for expiration");
        shop.setPromotedStatus(PROMOTED);
        ShopTier tier = tierRepository.findById(request.getTierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
        tier.setShopsPromoted(tier.getShopsPromoted() + 1);
        tierRepository.save(tier);
        shop.setShopTier(tier);
        shop.setLastPromotedAt(now());
        shopRepository.save(shop);
        return "Shop promoted";
    }

    @Override
    public String updatePhone(UpdatePhoneRequest request) {
        Shop shop = shopRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + request.getId()));
        shop.setPhone(request.getPhone());
        shopRepository.save(shop);
        return "Phone updated";
    }

    @Override
    public AccountDetailsResponse getShopAccount(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID"));
        return new AccountDetailsResponse(shop);
    }

    @Override
    public String updateShopAccount(Long shopId, UpdateAccountRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID"));
        shop.setAccountNumber(request.getAccountNumber());
        shop.setBankName(request.getBankName());
        shopRepository.save(shop);
        return "shop update success";
    }

    @Override
    public String addTier(AddTierRequest request) {
        ShopTier tier = new ShopTier();
        tier.setTierName(request.getTier());
        tier.setFeaturedNumber(request.getFeaturedNumber());
        tier.setFloatedNumber(request.getFloatedNumber());
        tier.setPromotedNumber(request.getPromotedNumber());
        tier.setPrice(request.getPrice());
        tierRepository.save(tier);
        return "tier added successfully";
    }

    @Override
    public String updateTier(Long id,AddTierRequest request) {
        ShopTier tier = tierRepository.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("tier not fount"));
        tier.setTierName(request.getTier());
        tier.setFeaturedNumber(request.getFeaturedNumber());
        tier.setFloatedNumber(request.getFloatedNumber());
        tier.setPromotedNumber(request.getPromotedNumber());
        tier.setPrice(request.getPrice());
        tierRepository.save(tier);
        return "tier updated  successfully";
    }

    @Override
    public List<TierResponse> getAllTiers() {
        List<ShopTier> tiers = tierRepository.findAll();
        if(tiers.isEmpty())return List.of();
        return tiers.stream()
                .map(TierResponse::new).toList();
    }

    @Override
    public TierResponse getTier(Long tierId) {
        ShopTier tier = tierRepository.findById(tierId)
                .orElseThrow(()->new ResourceNotFoundException("tier not found"));
        return new TierResponse(tier);
    }

    @Override
    public String deleteTier(Long tierId) {
        ShopTier tier = tierRepository.findById(tierId)
                .orElseThrow(()->new ResourceNotFoundException("tier not found"));
        tierRepository.delete(tier);
        return "tier deleted";
    }

    @Override
    public TierResponse getShopTier(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (shop.getPromotedStatus() == PromotedStatus.NOT_PROMOTED)
            throw new BusinessException("Shop not promoted");
        ShopTier tier = shop.getShopTier();
        return new TierResponse(tier);
    }

    @Override
    public List<ShopResponse> getAllPromoted() {
        List<Shop> shops = shopRepository.findPromotedShops(PROMOTED);
        if (shops.isEmpty())return List.of();
        return shops.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getAllByActiveShopsCount() {
        List<Shop> shops = shopRepository.findActiveShops(VERIFIED);
        return (long) shops.size();
    }

    @Override
    public List<Map<String, Object>> searchShopsWithProduct(String product, String market) {
        return List.of();
    }

    @Override
    public List<ShopResponse> getPlatinumShops() {
        List<Shop> shops = shopRepository.findPlatinumShops("PLATINUM");
        if (shops.isEmpty())return List.of();
        return shops.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String activateShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
        shop.setStatus(VERIFIED);
        shopRepository.save(shop);
        return "shop activated";
    }

    private ShopResponse mapToDto(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .logoUrl(shop.getLogoImage())
                .shopNumber(shop.getShopNumber())
                .homeAddress(shop.getHomeAddress())
                .streetName(shop.getStreetName())
                .cacNumber(shop.getCacNumber())
                .taxIdNumber(shop.getTaxIdNumber())
                .nin(shop.getNin())
                .bankName(shop.getBankName())
                .accountNumber(shop.getAccountNumber())
                .market(shop.getMarket().getName())
                .marketSection(shop.getMarketSection().getName())
                .firstName(shop.getUser().getFirstName())
                .lastName(shop.getUser().getLastName())
                .status(String.valueOf(shop.getStatus()))
                .promotedStatus(String.valueOf(shop.getPromotedStatus()))
                .totalPayoutAmount(shop.getTotalPayoutAmount().doubleValue())
                .phone(shop.getPhone())
                .featuredNumber(shop.getFeaturedNumber())
                .promotedNumber(shop.getPromotedNumber())
                .floatedNumber(shop.getFloatedNumber())
                .build();
    }

}
