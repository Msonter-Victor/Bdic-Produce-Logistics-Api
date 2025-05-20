package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.StateDTO;
import dev.gagnon.Model.State;
import dev.gagnon.Repository.StateRepository;
import dev.gagnon.Service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    private StateDTO mapToDTO(State state) {
        return StateDTO.builder()
                .id(state.getId())
                .name(state.getName())
                .build();
    }

    private State mapToEntity(StateDTO dto) {
        return State.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    @Override
    public StateDTO createState(StateDTO dto) {
        State state = mapToEntity(dto);
        return mapToDTO(stateRepository.save(state));
    }

    @Override
    public List<StateDTO> getAllStates() {
        return stateRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StateDTO getStateById(Long id) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State not found"));
        return mapToDTO(state);
    }

    @Override
    public StateDTO updateState(Long id, StateDTO dto) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State not found"));
        state.setName(dto.getName());
        return mapToDTO(stateRepository.save(state));
    }

    @Override
    public void deleteState(Long id) {
        if (!stateRepository.existsById(id)) {
            throw new RuntimeException("State not found");
        }
        stateRepository.deleteById(id);
    }
}
