package dev.gagnon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "market_sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    private String name;
    private String description;


    @OneToMany(mappedBy = "marketSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Shop> shops = new ArrayList<>();


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
