package dev.gagnon.Controller;

import dev.gagnon.DTO.StateDTO;
import dev.gagnon.Service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;

    @PostMapping("/add")
    public ResponseEntity<StateDTO> create(@RequestBody StateDTO dto) {
        return ResponseEntity.ok(stateService.createState(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StateDTO>> getAll() {
        return ResponseEntity.ok(stateService.getAllStates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(stateService.getStateById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StateDTO> update(@PathVariable Long id, @RequestBody StateDTO dto) {
        return ResponseEntity.ok(stateService.updateState(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stateService.deleteState(id);
        return ResponseEntity.noContent().build();
    }
}
