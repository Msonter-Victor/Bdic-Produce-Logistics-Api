package dev.gagnon.dto.response;

import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.LogisticsCompany;
import dev.gagnon.data.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private Long id;
    private String engineNumber;
    private String plateNumber;
    private String type;
    private String riderName;

    public VehicleResponse(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.engineNumber = vehicle.getEngineNumber();
        this.plateNumber = vehicle.getPlateNumber();
        this.type = String.valueOf(vehicle.getType());

        if (vehicle.getRider() != null) {
            this.riderName = vehicle.getRider().getFirstName() + " " + vehicle.getRider().getLastName();
        } else {
            this.riderName = "";
        }
    }
}
