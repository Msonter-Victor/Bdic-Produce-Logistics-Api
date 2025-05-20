package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.MarketSectionDto;
import dev.gagnon.Model.Market;
import dev.gagnon.Model.MarketSection;
import dev.gagnon.Repository.MarketRepository;
import dev.gagnon.Repository.MarketSectionRepository;
import dev.gagnon.Service.MarketSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketSectionServiceImpl implements MarketSectionService {
    private final MarketSectionRepository sectionRepository;
    private final MarketRepository marketRepository;

    @Override
    public MarketSectionDto createSection(MarketSectionDto dto) {
        Market market = marketRepository.findById(dto.getMarketId())
                .orElseThrow(() -> new RuntimeException("Market not found"));

        MarketSection section = MarketSection.builder()
                .market(market)
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MarketSection saved = sectionRepository.save(section);
        return mapToDto(saved);
    }

    @Override
    public MarketSectionDto getSectionById(Long id) {
        return sectionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Market Section not found"));
    }

    @Override
    public List<MarketSectionDto> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MarketSectionDto updateSection(Long id, MarketSectionDto dto) {
        MarketSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market Section not found"));

        section.setName(dto.getName());
        section.setDescription(dto.getDescription());
        section.setUpdatedAt(LocalDateTime.now());

        MarketSection updated = sectionRepository.save(section);
        return mapToDto(updated);
    }

    @Override
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    private MarketSectionDto mapToDto(MarketSection section) {
        return MarketSectionDto.builder()
                .id(section.getId())
                .marketId(section.getMarket().getId())
                .name(section.getName())
                .description(section.getDescription())
                .createdAt(section.getCreatedAt() != null ? section.getCreatedAt().toString() : null)
                .updatedAt(section.getUpdatedAt() != null ? section.getUpdatedAt().toString() : null)
                .build();
    }
}
