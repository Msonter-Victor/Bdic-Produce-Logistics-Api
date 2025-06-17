package dev.gagnon.dto.request;

import dev.gagnon.data.constants.Type;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVehicleRequest {
    private String ownerEmail;
    private String engineNumber;
    private String plateNumber;
    private String type;
}
