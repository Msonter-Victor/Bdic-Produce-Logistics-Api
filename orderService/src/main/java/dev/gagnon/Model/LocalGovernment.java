package dev.gagnon.Model;
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
    private State state;

    @Column(nullable = false)
    private String name;
}
