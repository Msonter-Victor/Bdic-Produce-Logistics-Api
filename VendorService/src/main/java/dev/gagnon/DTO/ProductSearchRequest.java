package dev.gagnon.DTO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductSearchRequest {
private String productName;
private String categoryName;
private String shopName;
private String marketName;
}
