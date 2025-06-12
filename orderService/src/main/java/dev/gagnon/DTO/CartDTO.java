package dev.gagnon.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private Long statusId;
    private List<CartItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
