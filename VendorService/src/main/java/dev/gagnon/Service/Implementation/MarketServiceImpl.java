package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.MarketDto;
import dev.gagnon.Model.CouncilWard;
import dev.gagnon.Model.Market;
import dev.gagnon.Repository.CouncilWardRepository;
import dev.gagnon.Repository.MarketRepository;
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

    @Override
    public MarketDto createMarket(MarketDto dto) {
        CouncilWard councilWard = councilWardRepository.findById(dto.getCouncilWardId())
                .orElseThrow(() -> new RuntimeException("Council Ward not found"));
        Market market = Market.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .lines(dto.getLines())
                .shops(dto.getShops())
                .councilWard(councilWard)
                .build();

        Market saved = marketRepository.save(market);
        return mapToDto(saved);
    }

    @Override
    public MarketDto getMarketById(Long id) {
        return marketRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Market not found"));
    }

    @Override
    public List<MarketDto> getAllMarkets() {
        return marketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MarketDto updateMarket(Long id, MarketDto dto) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));

        CouncilWard councilWard = councilWardRepository.findById(dto.getCouncilWardId())
                .orElseThrow(() -> new RuntimeException("Council Ward not found"));

        market.setName(dto.getName());
        market.setAddress(dto.getAddress());
        market.setCity(dto.getCity());
        market.setLines(dto.getLines());
        market.setShops(dto.getShops());
        market.setCouncilWard(councilWard);

        Market updated = marketRepository.save(market);
        return mapToDto(updated);
    }

    @Override
    public void deleteMarket(Long id) {
        marketRepository.deleteById(id);
    }

    private MarketDto mapToDto(Market market) {
        return MarketDto.builder()
                .id(market.getId())
                .name(market.getName())
                .address(market.getAddress())
                .city(market.getCity())
                .lines(market.getLines())
                .shops(market.getShops())
                .councilWardId(
                        market.getCouncilWard() != null ? market.getCouncilWard().getId() : null
                )
                .build();
    }
}
