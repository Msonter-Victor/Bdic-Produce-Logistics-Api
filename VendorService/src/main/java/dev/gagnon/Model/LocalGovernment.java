package dev.gagnon.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_governments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalGovernment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @JsonIgnore
    private State state;

    @Column(nullable = false)
    private String name;
}
