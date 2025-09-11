package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.MarketSectionDto;
import dev.gagnon.DTO.request.AddSectionRequest;
import dev.gagnon.DTO.request.UpdateSectionRequest;
import dev.gagnon.DTO.response.MarketSectionResponse;
import dev.gagnon.Exception.BusinessException;
import dev.gagnon.Model.Market;
import dev.gagnon.Model.MarketSection;
import dev.gagnon.Model.Product;
import dev.gagnon.Model.Shop;
import dev.gagnon.Repository.MarketRepository;
import dev.gagnon.Repository.MarketSectionRepository;
import dev.gagnon.Repository.ProductRepository;
import dev.gagnon.Repository.ShopRepository;
import dev.gagnon.Service.MarketSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static dev.gagnon.Model.constants.MarketStatus.ACTIVE;
import static dev.gagnon.Model.constants.MarketStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class MarketSectionServiceImpl implements MarketSectionService {
    private final MarketSectionRepository sectionRepository;
    private final MarketRepository marketRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    @Override
    public String createSection(AddSectionRequest dto) {
        Market market = marketRepository.findById(dto.getMarketId())
                .orElseThrow(() -> new RuntimeException("Market not found"));
        MarketSection section = MarketSection.builder()
                .market(market)
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MarketSection saved = sectionRepository.save(section);
        return "section added";
    }

    @Override
    public MarketSectionResponse getSectionById(Long id) {
        return sectionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Market Section not found"));
    }

    @Override
    public List<MarketSectionResponse> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String updateSection(Long id, UpdateSectionRequest dto) {
        MarketSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market Section not found"));
        section.setName(dto.getName());
        MarketSection updated = sectionRepository.save(section);
        return updated.getName() + " updated";
    }

    
    @Override
    public void deleteSection(Long id) {
        MarketSection section = sectionRepository.findById(id)
                .orElseThrow(()->new BusinessException("section not found"));
        List<Shop> shops = shopRepository.findAllBySectionId(id);
        List<List<Product>> list = shops.stream()
                .map(shop -> productRepository.findAllByShop(shop.getId())).toList();
        list.forEach(productRepository::deleteAllInBatch);
        shopRepository.deleteAll(shops);
        sectionRepository.delete(section);
    }

    @Override
    public List<MarketSectionResponse> getAllSectionsForMarket(Long marketId) {
        List<MarketSection> sections = sectionRepository.findAlByMarket(marketId);
        if (sections.isEmpty()) return List.of();
        return sections.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getAllSectionsCount() {
        return (long) marketSectionRepository.findAll().size();
    }

    @Override
    public String activateLine(Long id) {
        MarketSection section = sectionRepository.findById(id)
                .orElseThrow(()->new BusinessException("section not found"));
        section.setStatus(ACTIVE);
        sectionRepository.save(section);
        return "section activated";
    }

    @Override
    public String deActivateLine(Long id) {
        MarketSection section = sectionRepository.findById(id)
                .orElseThrow(()->new BusinessException("section not found"));
        section.setStatus(INACTIVE);
        sectionRepository.save(section);
        return "section deactivated";
    }

    private MarketSectionDto mapToDto(MarketSection section) {
        return MarketSectionDto.builder()
                .id(section.getId())
                .marketId(section.getMarket().getId())
                .name(section.getName())
                .createdAt(section.getCreatedAt() != null ? section.getCreatedAt().toString() : null)
                .updatedAt(section.getUpdatedAt() != null ? section.getUpdatedAt().toString() : null)
                .build();
    }
    private MarketSectionResponse mapToResponse(MarketSection section) {
        List<Shop>shops = shopRepository.findAllBySectionId(section.getId());
        return
                MarketSectionResponse.builder()
                .id(section.getId())
                .marketId(section.getMarket().getId())
                        .status(String.valueOf(section.getStatus()))
                .name(section.getName())
                        .shops((long) shops.size())
                .createdAt(section.getCreatedAt() != null ? section.getCreatedAt().toString() : null)
                .updatedAt(section.getUpdatedAt() != null ? section.getUpdatedAt().toString() : null)
                .build();
    }
}
