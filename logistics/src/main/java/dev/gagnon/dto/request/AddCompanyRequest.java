package dev.gagnon.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCompanyRequest {
    private String ownerEmail;
    private String companyName;
    private String companyAddress;
    private String ownerName;
    private MultipartFile logo;
    private MultipartFile cacImage;
    private List<MultipartFile> otherDocuments;
    private String cacNumber;
    private String tin;
    private String bankName;
    private String accountNumber;
}
