package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.MarketDto;
import dev.gagnon.DTO.ShopDto;
import dev.gagnon.DTO.request.AddMarketRequest;
import dev.gagnon.DTO.request.UpdateMarketRequest;
import dev.gagnon.DTO.response.MarketResponse;
import dev.gagnon.Model.*;
import dev.gagnon.Model.constants.MarketStatus;
import dev.gagnon.Repository.*;
import dev.gagnon.Service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final MarketRepository marketRepository;
    private final CouncilWardRepository councilWardRepository;
    private final ShopRepository shopRepository;
    private final LocalGovernmentRepository localGovernmentRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final ProductRepository productRepository;

    @Override
    public MarketResponse createMarket(AddMarketRequest request) {
        CouncilWard councilWard = councilWardRepository.findById(request.getCouncilId())
                .orElseThrow(() -> new RuntimeException("Council Ward not found"));
        LocalGovernment localGovernment = localGovernmentRepository.findById(request.getLocalGovernmentId())
                .orElseThrow(() -> new RuntimeException("Local Government not found"));
        Market market = Market.builder()
                .name(request.getMarketName())
                .address(request.getLocation())
                .status(MarketStatus.ACTIVE)
                .city(localGovernment.getName())
                .lines(Math.toIntExact(request.getLines()))
                .build();
        Market saved = marketRepository.save(market);
        councilWard.setMarket(saved);
        councilWardRepository.save(councilWard);
        return mapToDto(saved);
    }



    @Override
    public MarketResponse getMarketById(Long id) {
        return marketRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Market not found"));
    }

    @Override
    public List<MarketResponse> getAllMarkets() {
        return marketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MarketResponse updateMarket(Long id, UpdateMarketRequest dto) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));
        market.setName(dto.getName());
        Market updated = marketRepository.save(market);
        return mapToDto(updated);
    }

    @Override
    public void deleteMarket(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));
        // Delete all related sections first
        List<MarketSection>sections =  marketSectionRepository.findAlByMarket(id);
        CouncilWard ward = councilWardRepository.findByMarket(id);
        List<Shop> shops = sections.stream().map(section -> section.getShop()).toList();
        List<List<Product>> list = shops.stream()
                .map(shop -> productRepository.findAllByShop(shop.getId())).toList();
        list.forEach(productRepository::deleteAllInBatch);
        shopRepository.deleteAll(shops);
        councilWardRepository.delete(ward);
        marketSectionRepository.deleteAllInBatch(sections);
        marketRepository.delete(market);
    }

    @Override
    public List<Market> saveAll() {
        List<Market> markets = marketRepository.findAll();
        marketRepository.saveAll(markets);
        return markets;
    }

    @Override
    public String deActivateMarket(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));
        market.setStatus(MarketStatus.INACTIVE);
        marketRepository.save(market);
        return "market deactivated";
    }

    @Override
    public Long getActiveMarkets() {
        List<Market> getMarkets = marketRepository.findAll();
        return getMarkets.stream()
                .filter(market -> market.getStatus()==MarketStatus.ACTIVE)
                .count();
    }

    @Override
    public String activateMarket(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));
        market.setStatus(MarketStatus.ACTIVE);
        marketRepository.save(market);
        return "market activated";
    }

    private MarketResponse mapToDto(Market market) {
        List<Shop> shops = shopRepository.findAllByMarketId(market.getId());
        List<ShopDto> shopDtos = shops.stream()
                .map(this::mapShopResponse)
                .toList();
        List<MarketSection> sections = marketSectionRepository.findAlByMarket(market.getId());
        Integer lines = sections.size();
        return MarketResponse.builder()
                .id(market.getId())
                .status(String.valueOf(market.getStatus()))
                .name(market.getName())
                .address(market.getAddress())
                .city(market.getCity())
                .lines(lines)
                .shops(shopDtos)
                .numberOfShops(shops.size())
                .build();
    }

    private ShopDto mapShopResponse(Shop shop) {
        ShopDto shopDto = new ShopDto();
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setAddress(shop.getAddress());
        shopDto.setMarketId(shop.getMarket().getId());
        shopDto.setShopNumber(shop.getShopNumber());
        shopDto.setMarketId(shop.getMarket().getId());
        shopDto.setBankName(shop.getBankName());
        shopDto.setAccountNumber(shop.getAccountNumber());
        shopDto.setNin(shop.getNin());
        shopDto.setPhone(shop.getPhone());
        shopDto.setCacNumber(shop.getCacNumber());
        shopDto.setStreetName(shop.getStreetName());
        shopDto.setTaxIdNumber(shopDto.getTaxIdNumber());
        return shopDto;
    }
}
