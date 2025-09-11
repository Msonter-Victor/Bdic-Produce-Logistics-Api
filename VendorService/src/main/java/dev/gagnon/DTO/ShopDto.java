package dev.gagnon.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private String phone;

    private String shopNumber;
    private String homeAddress;
    private String streetName;
    private String cacNumber;
    private String taxIdNumber;
    private String nin;
    private String bankName;
    private String accountNumber;

    private Long marketId;
    private String marketSectionId;
    private String email;
    private String code;
}
