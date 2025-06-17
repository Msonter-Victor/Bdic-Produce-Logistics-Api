package dev.gagnon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDeliveryRequest {
    private String orderNumber;
    private Long riderId;
}
