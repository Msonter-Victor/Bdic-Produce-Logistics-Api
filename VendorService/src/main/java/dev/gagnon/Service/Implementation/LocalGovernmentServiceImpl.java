package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.LocalGovernmentDTO;
import dev.gagnon.Model.LocalGovernment;
import dev.gagnon.Model.State;
import dev.gagnon.Repository.LocalGovernmentRepository;
import dev.gagnon.Repository.StateRepository;
import dev.gagnon.Service.LocalGovernmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalGovernmentServiceImpl implements LocalGovernmentService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final StateRepository stateRepository;

    private LocalGovernmentDTO mapToDTO(LocalGovernment lg) {
        return LocalGovernmentDTO.builder()
                .id(lg.getId())
                .name(lg.getName())
                .stateId(lg.getState().getId())
                .build();
    }

    private LocalGovernment mapToEntity(LocalGovernmentDTO dto) {
        State state = stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        return LocalGovernment.builder()
                .id(dto.getId())
                .name(dto.getName())
                .state(state)
                .build();
    }

    @Override
    public LocalGovernmentDTO create(LocalGovernmentDTO dto) {
        LocalGovernment lg = mapToEntity(dto);
        return mapToDTO(localGovernmentRepository.save(lg));
    }

    @Override
    public List<LocalGovernmentDTO> getAll() {
        return localGovernmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LocalGovernmentDTO getById(Long id) {
        LocalGovernment lg = localGovernmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local Government not found"));
        return mapToDTO(lg);
    }

    @Override
    public LocalGovernmentDTO update(Long id, LocalGovernmentDTO dto) {
        LocalGovernment existing = localGovernmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local Government not found"));

        existing.setName(dto.getName());

        if (!existing.getState().getId().equals(dto.getStateId())) {
            State newState = stateRepository.findById(dto.getStateId())
                    .orElseThrow(() -> new RuntimeException("State not found"));
            existing.setState(newState);
        }

        return mapToDTO(localGovernmentRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!localGovernmentRepository.existsById(id)) {
            throw new RuntimeException("Local Government not found");
        }
        localGovernmentRepository.deleteById(id);
    }
}
