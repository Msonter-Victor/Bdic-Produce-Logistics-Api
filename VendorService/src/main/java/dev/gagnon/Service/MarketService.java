package dev.gagnon.Service;

import dev.gagnon.DTO.MarketDto;
import java.util.List;

public interface MarketService {
    MarketDto createMarket(MarketDto dto);
    MarketDto getMarketById(Long id);
    List<MarketDto> getAllMarkets();
    MarketDto updateMarket(Long id, MarketDto dto);
    void deleteMarket(Long id);
}
