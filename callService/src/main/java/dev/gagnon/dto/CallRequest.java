package dev.gagnon.dto;

import lombok.Data;

@Data
public class CallRequest {
    private String callerEmail;
    private String callerName;
    private String vendorEmail;
    private String vendorName;
    private String productName;
    private String callType;
}