package dev.gagnon.Service;


import dev.gagnon.DTO.LocalGovernmentDTO;

import java.util.List;

public interface LocalGovernmentService {
    LocalGovernmentDTO create(LocalGovernmentDTO dto);
    List<LocalGovernmentDTO> getAll();
    LocalGovernmentDTO getById(Long id);
    LocalGovernmentDTO update(Long id, LocalGovernmentDTO dto);
    void delete(Long id);
}
