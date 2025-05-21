package dev.gagnon.DTO;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private String logoImage;
    private String shopNumber;
    private String homeAddress;
    private String streetName;
    private String cacNumber;
    private String taxIdNumber;
    private String nin;
    private String bankName;
    private String accountNumber;

    private Long marketId;
    private Long marketSectionId;
    private Long userId;
    private Long statusId;
    private MultipartFile mediaFile;

}
