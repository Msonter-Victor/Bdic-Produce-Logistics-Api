package dev.gagnon.DTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusRequestDTO {
    @NotBlank(message = "Status name is required")
    private String name;
}
