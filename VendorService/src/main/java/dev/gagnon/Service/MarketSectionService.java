package dev.gagnon.Service;

import dev.gagnon.DTO.MarketSectionDto;
import dev.gagnon.DTO.request.AddSectionRequest;
import dev.gagnon.DTO.request.UpdateSectionRequest;
import dev.gagnon.DTO.response.MarketSectionResponse;

import java.util.List;

public interface MarketSectionService {
    String createSection(AddSectionRequest dto);
    MarketSectionResponse getSectionById(Long id);
    List<MarketSectionResponse> getAllSections();
    String updateSection(Long id, UpdateSectionRequest dto);
    void deleteSection(Long id);

    List<MarketSectionResponse> getAllSectionsForMarket(Long marketId);

    Long getAllSectionsCount();

    String activateLine(Long id);

    String deActivateLine(Long id);
}
