package dev.gagnon.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {

    @Enumerated(EnumType.STRING)
    private DeliveryMethod method;
    
    private String address;
    private String contactPhone;
    
    //For shop pickup
    private String shopLocation;
    private LocalDateTime pickupTime;
}