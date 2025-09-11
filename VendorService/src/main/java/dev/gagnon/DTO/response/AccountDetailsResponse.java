package dev.gagnon.DTO.response;

import dev.gagnon.Model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponse {
    private String accountNumber;
    private String bankName;
    public AccountDetailsResponse(Shop shop){
        this.accountNumber = shop.getAccountNumber();
        this.bankName = shop.getBankName();
    }
}
