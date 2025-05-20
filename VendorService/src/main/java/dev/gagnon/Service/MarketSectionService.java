package dev.gagnon.Service;

import dev.gagnon.DTO.MarketSectionDto;

import java.util.List;

public interface MarketSectionService {
    MarketSectionDto createSection(MarketSectionDto dto);
    MarketSectionDto getSectionById(Long id);
    List<MarketSectionDto> getAllSections();
    MarketSectionDto updateSection(Long id, MarketSectionDto dto);
    void deleteSection(Long id);
}
