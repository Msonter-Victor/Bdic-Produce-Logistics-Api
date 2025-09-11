package dev.gagnon.data.model;

import dev.gagnon.data.constants.RideCurrentStatus;
import dev.gagnon.data.constants.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String engineNumber;
    private String plateNumber;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private LogisticsCompany company;
    @Enumerated(EnumType.STRING)
    private RideCurrentStatus status;
    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;
    @Enumerated(EnumType.STRING)
    private Type type;
}
