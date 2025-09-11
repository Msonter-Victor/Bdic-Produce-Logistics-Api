package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String companyAddress;
    private String ownerEmail;
    private String ownerName;
    private Long fleetNumber;
    private String logoUrl;
    private String cacImageUrl;
    private List<String> otherDocumentUrls;
    private String cacNumber;
    private String tin;
}

