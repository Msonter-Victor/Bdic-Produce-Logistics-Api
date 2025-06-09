package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RiderRequest {
    private Long vehicleId;
    private String riderFirstName;
    private String riderLastName;
    private String riderEmail;
}
