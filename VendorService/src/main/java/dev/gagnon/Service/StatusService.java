package dev.gagnon.Service;

import dev.gagnon.DTO.StatusRequestDTO;
import dev.gagnon.DTO.response.StatusResponseDTO;
import dev.gagnon.Model.Status;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.StatusRepository;
import dev.gagnon.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatusService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;
//------------------------------------------------------------------------------------
    public StatusResponseDTO createStatus(StatusRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Status status = Status.builder()
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .build();

        Status saved = statusRepository.save(status);
        return toResponseDTO(saved);
    }
//--------------------------------------------------------------------------------------
    public List<StatusResponseDTO> getAllStatuses() {
        return statusRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
//------------------------------------------------------------------------------------
    public Optional<StatusResponseDTO> getStatusById(Long id) {
        return statusRepository.findById(id)
                .map(this::toResponseDTO);
    }
//----------------------------------------------------------------------------------------
    public StatusResponseDTO updateStatus(Long id, StatusRequestDTO dto) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        status.setName(dto.getName());
        Status updated = statusRepository.save(status);

        return toResponseDTO(updated);
    }
//---------------------------------------------------------------------------------
    public void deleteStatus(Long id) {
        if (!statusRepository.existsById(id)) {
            throw new RuntimeException("Status not found");
        }
        statusRepository.deleteById(id);
    }
    //-------------------------------------------------------------------------------
    private StatusResponseDTO toResponseDTO(Status status) {
        return StatusResponseDTO.builder()
                .id(status.getId())
                .name(status.getName())
                .createdAt(status.getCreatedAt())
                .build();
    }

}
