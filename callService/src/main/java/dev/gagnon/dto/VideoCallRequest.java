package dev.gagnon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallRequest {
    private String buyerEmail;
    private String vendorEmail;
    private Long productId;
    private Long shopId;
    private String productName;
    private String shopName;
}