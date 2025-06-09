package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyResponse {
    private Long id;
    private String companyName;
    private String companyAddress;
    private String ownerEmail;
}
