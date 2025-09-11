package dev.gagnon.DTO.response;


import dev.gagnon.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductResponse {
    private Long id;
    private String message;
}
