package dev.gagnon.dto.response;

import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.LogisticsCompany;
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
    private Type type;
}
