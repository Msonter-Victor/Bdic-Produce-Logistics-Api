package dev.gagnon.Service.Implementation;


import dev.gagnon.DTO.CouncilWardDTO;
import dev.gagnon.Model.CouncilWard;
import dev.gagnon.Model.LocalGovernment;
import dev.gagnon.Repository.CouncilWardRepository;
import dev.gagnon.Repository.LocalGovernmentRepository;
import dev.gagnon.Service.CouncilWardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouncilWardServiceImpl implements CouncilWardService {

    private final CouncilWardRepository councilWardRepository;
    private final LocalGovernmentRepository localGovernmentRepository;

    private CouncilWardDTO mapToDTO(CouncilWard cw) {
        return CouncilWardDTO.builder()
                .id(cw.getId())
                .name(cw.getName())
                .code(cw.getCode())
                .localGovernmentId(cw.getLocalGovernment().getId())
                .build();
    }

    private CouncilWard mapToEntity(CouncilWardDTO dto) {
        LocalGovernment lg = localGovernmentRepository.findById(dto.getLocalGovernmentId())
                .orElseThrow(() -> new RuntimeException("Local Government not found"));

        return CouncilWard.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .localGovernment(lg)
                .build();
    }

    @Override
    public CouncilWardDTO create(CouncilWardDTO dto) {
        CouncilWard entity = mapToEntity(dto);
        return mapToDTO(councilWardRepository.save(entity));
    }

    @Override
    public List<CouncilWardDTO> getAll() {
        return councilWardRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouncilWardDTO getById(Long id) {
        CouncilWard cw = councilWardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Council Ward not found"));
        return mapToDTO(cw);
    }

    @Override
    public CouncilWardDTO update(Long id, CouncilWardDTO dto) {
        CouncilWard existing = councilWardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Council Ward not found"));

        existing.setName(dto.getName());
        existing.setCode(dto.getCode());

        if (!existing.getLocalGovernment().getId().equals(dto.getLocalGovernmentId())) {
            LocalGovernment newLG = localGovernmentRepository.findById(dto.getLocalGovernmentId())
                    .orElseThrow(() -> new RuntimeException("Local Government not found"));
            existing.setLocalGovernment(newLG);
        }

        return mapToDTO(councilWardRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!councilWardRepository.existsById(id)) {
            throw new RuntimeException("Council Ward not found");
        }
        councilWardRepository.deleteById(id);
    }
}
