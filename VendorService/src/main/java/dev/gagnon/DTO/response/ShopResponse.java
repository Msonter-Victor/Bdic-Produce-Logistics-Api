package dev.gagnon.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopResponse {
    private Long id;
    private String name;
    private String address;
    private String logoUrl;
    private String phone;

    private String shopNumber;
    private String homeAddress;
    private String streetName;
    private String cacNumber;
    private String taxIdNumber;
    private String nin;
    private String bankName;
    private String accountNumber;

    private String market;
    private String marketSection;
    private String firstName;
    private String lastName;
    private String status;
    private double totalPayoutAmount;
    private String promotedStatus;
    private Long promotedTierId;
    private Long featuredNumber;
    private Long promotedNumber;
    private Long floatedNumber;
}
