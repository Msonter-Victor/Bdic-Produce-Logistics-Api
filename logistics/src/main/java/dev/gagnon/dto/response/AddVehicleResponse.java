package dev.gagnon.dto.response;

import dev.gagnon.data.constants.Type;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleResponse {
    private Long id;
    private String plateNumber;
    private String engineNumber;
    private Type type;
}
