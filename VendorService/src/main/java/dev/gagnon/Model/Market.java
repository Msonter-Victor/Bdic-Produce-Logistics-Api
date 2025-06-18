package dev.gagnon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "markets")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String city;

    private Integer lines;
//    private Integer shops;

    @ManyToOne
    @JoinColumn(name = "council_ward_id")
    private CouncilWard councilWard;

    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MarketSection> marketSections = new ArrayList<>();

    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Shop> shops = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
