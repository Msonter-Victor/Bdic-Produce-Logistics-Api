package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponse {
    private Long id;
    private String accountNumber;
    private String bankName;
}
