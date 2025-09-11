package dev.gagnon.Service;

import dev.gagnon.DTO.MarketDto;
import dev.gagnon.DTO.request.AddMarketRequest;
import dev.gagnon.DTO.request.UpdateMarketRequest;
import dev.gagnon.DTO.response.MarketResponse;
import dev.gagnon.Model.Market;

import java.util.List;

public interface MarketService {
    MarketResponse createMarket(AddMarketRequest request);
    MarketResponse getMarketById(Long id);
    List<MarketResponse> getAllMarkets();
    MarketResponse updateMarket(Long id, UpdateMarketRequest dto);
    void deleteMarket(Long id);

    List<Market> saveAll();

    String deActivateMarket(Long id);

    Long getActiveMarkets();

    String activateMarket(Long id);
}
