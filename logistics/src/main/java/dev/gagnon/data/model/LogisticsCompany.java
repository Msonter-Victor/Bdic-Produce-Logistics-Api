package dev.gagnon.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "logistics_companies")
public class LogisticsCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String companyAddress;
    private String ownerEmail;
    private String ownerName;
    private Long fleetNumber;
    private String logoUrl;
    private String cacImageUrl;

    @ElementCollection
    @CollectionTable(name = "logistics_company_documents", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "document_url")
    private List<String> otherDocumentUrls = new ArrayList<>();

    private String cacNumber;
    private String tin;
}

