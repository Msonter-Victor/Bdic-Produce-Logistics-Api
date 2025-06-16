package dev.gagnon.Service;

import dev.gagnon.DTO.ShopDto;
import dev.gagnon.Model.Shop;
import dev.gagnon.Repository.ShopRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {

     ShopDto createShop(ShopDto dto, MultipartFile logoImage);

    ShopDto getShopById(Long id);
    List<ShopDto> getAllShops();
    ShopDto updateShop(Long id, ShopDto dto);
    void deleteShop(Long id);
}