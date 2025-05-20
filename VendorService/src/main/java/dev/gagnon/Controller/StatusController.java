package dev.gagnon.Controller;

import dev.gagnon.DTO.StatusRequestDTO;
import dev.gagnon.DTO.StatusResponseDTO;
import dev.gagnon.Service.StatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping("/addStatus")
    public ResponseEntity<StatusResponseDTO> createStatus(@Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(statusService.createStatus(request));
    }

    @GetMapping("/viewAllStatus")
    public ResponseEntity<List<StatusResponseDTO>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<StatusResponseDTO> getStatusById(@PathVariable Long id) {
        return statusService.getStatusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StatusResponseDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequestDTO request) {
        return ResponseEntity.ok(statusService.updateStatus(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
