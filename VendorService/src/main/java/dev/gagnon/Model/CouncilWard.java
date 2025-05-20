package dev.gagnon.Model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    private LocalGovernment localGovernment;

    @OneToMany(mappedBy = "councilWard", cascade = CascadeType.ALL)
    private List<Market> markets;
}
