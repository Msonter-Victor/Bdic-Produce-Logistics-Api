package dev.gagnon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "council_wards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouncilWard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_government_id", nullable = false)
    @JsonIgnore
    private LocalGovernment localGovernment;
    @ManyToOne
    @JoinColumn(name = "market_id")
    private Market market;
}
