package dev.gagnon.Service;

import dev.gagnon.DTO.StateDTO;
import java.util.List;

public interface StateService {
    StateDTO createState(StateDTO dto);
    List<StateDTO> getAllStates();
    StateDTO getStateById(Long id);
    StateDTO updateState(Long id, StateDTO dto);
    void deleteState(Long id);
}
