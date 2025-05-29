package dev.gagnon.Service.Implementation;

import com.cloudinary.Cloudinary;
import dev.gagnon.DTO.ShopDto;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.*;
import dev.gagnon.Repository.*;
import dev.gagnon.Service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.gagnon.Util.ServiceUtils.getMediaUrl;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;
    private final MarketSectionRepository marketSectionRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final Cloudinary cloudinary;

    @Value("${app.upload.dir:${user.dir}/uploads}") // Default directory for local storage
    private String uploadDir;

    @Override
    public ShopDto createShop(ShopDto dto, MultipartFile logoImage) {
        String imagePath = null;

        // Handle file upload if provided
        if (logoImage != null && !logoImage.isEmpty()) {
            try {
                String uniqueFilename = UUID.randomUUID() + "_" + logoImage.getOriginalFilename();
                File destinationFile = new File(uploadDir, uniqueFilename);
                destinationFile.getParentFile().mkdirs(); // Ensure the directory exists
                logoImage.transferTo(destinationFile);
                imagePath = "/uploads/" + uniqueFilename; // Path to save in DB
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload logo image", e);
            }
        }

        // Build and save the shop entity
        Shop shop = Shop.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .logoImage(imagePath) // This is stored in the entity, not the DTO
                .shopNumber(dto.getShopNumber())
                .homeAddress(dto.getHomeAddress())
                .streetName(dto.getStreetName())
                .cacNumber(dto.getCacNumber())
                .taxIdNumber(dto.getTaxIdNumber())
                .nin(dto.getNin())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .market(marketRepository.findById(dto.getMarketId())
                        .orElseThrow(() -> new ResourceNotFoundException("Market not found with ID: " + dto.getMarketId())))
                .marketSection(marketSectionRepository.findById(dto.getMarketSectionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Market section not found with ID: " + dto.getMarketSectionId())))
                .user(userRepository.findById(dto.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId())))
                .status(statusRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Status not found with ID: " + dto.getStatusId())))
                .build();

        return mapToDto(shopRepository.save(shop));
    }

    @Override
    public ShopDto getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));
        return mapToDto(shop);
    }

    @Override
    public List<ShopDto> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShopDto updateShop(Long id, ShopDto dto) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));

        shop.setName(dto.getName());
        shop.setAddress(dto.getAddress());
        shop.setShopNumber(dto.getShopNumber());
        shop.setHomeAddress(dto.getHomeAddress());
        shop.setStreetName(dto.getStreetName());
        shop.setCacNumber(dto.getCacNumber());
        shop.setTaxIdNumber(dto.getTaxIdNumber());
        shop.setNin(dto.getNin());
        shop.setBankName(dto.getBankName());
        shop.setAccountNumber(dto.getAccountNumber());

        shop.setMarket(marketRepository.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Market not found with ID: " + dto.getMarketId())));
        shop.setMarketSection(marketSectionRepository.findById(dto.getMarketSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Market section not found with ID: " + dto.getMarketSectionId())));
        shop.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId())));
        shop.setStatus(statusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with ID: " + dto.getStatusId())));

        return mapToDto(shopRepository.save(shop));
    }

    @Override
    public void deleteShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + id));
        shopRepository.delete(shop);
    }

    private ShopDto mapToDto(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                // .logoImage(shop.getLogoImage()) <-- Removed since not in DTO
                .shopNumber(shop.getShopNumber())
                .homeAddress(shop.getHomeAddress())
                .streetName(shop.getStreetName())
                .cacNumber(shop.getCacNumber())
                .taxIdNumber(shop.getTaxIdNumber())
                .nin(shop.getNin())
                .bankName(shop.getBankName())
                .accountNumber(shop.getAccountNumber())
                .marketId(shop.getMarket().getId())
                .marketSectionId(shop.getMarketSection().getId())
                .userId(shop.getUser().getId())
                .statusId(shop.getStatus().getId())
                .createdAt(shop.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
