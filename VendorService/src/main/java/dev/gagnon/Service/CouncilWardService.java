package dev.gagnon.Service;

import dev.gagnon.DTO.CouncilWardDTO;
import java.util.List;

public interface CouncilWardService {
    CouncilWardDTO create(CouncilWardDTO dto);
    List<CouncilWardDTO> getAll();
    CouncilWardDTO getById(Long id);
    CouncilWardDTO update(Long id, CouncilWardDTO dto);
    void delete(Long id);
}
