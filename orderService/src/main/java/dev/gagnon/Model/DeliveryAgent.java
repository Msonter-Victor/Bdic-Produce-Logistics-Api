package dev.gagnon.Model;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@Table(name = "DeliveryAgents")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String logisticCompany;

    @OneToMany(mappedBy = "agent")
    private List<Delivery> deliveries;
}
