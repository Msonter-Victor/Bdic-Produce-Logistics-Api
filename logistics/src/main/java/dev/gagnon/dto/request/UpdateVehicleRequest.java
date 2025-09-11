package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleRequest {
    private String engineNumber;
    private String plateNumber;
    private String type;
}
