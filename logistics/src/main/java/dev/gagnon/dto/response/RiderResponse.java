package dev.gagnon.dto.response;

import dev.gagnon.data.constants.RideCurrentStatus;
import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.LogisticsCompany;
import dev.gagnon.data.model.Rider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RiderResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String status;
    private String type;
    public RiderResponse(Rider rider){
        this.id = rider.getId();
        this.firstName = rider.getFirstName();
        this.lastName = rider.getLastName();
        this.email = rider.getEmail();
        this.phone = rider.getPhone();
    }
}
