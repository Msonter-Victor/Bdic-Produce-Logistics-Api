package dev.gagnon.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDetailsRequest {
    private Long companyId;
    private String bankName;
    private String accountNumber;
}
