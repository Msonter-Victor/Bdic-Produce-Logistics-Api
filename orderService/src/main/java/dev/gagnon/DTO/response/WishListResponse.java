package dev.gagnon.dto.response;

import dev.gagnon.model.WishListItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WishListResponse {
    private Long id;
    private String buyerEmail;
    private List<WishListItem> wishList;
}
