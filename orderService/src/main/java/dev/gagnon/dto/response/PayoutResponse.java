package dev.gagnon.dto.response;

import dev.gagnon.model.Payout;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PayoutResponse {
    private Long id;
    private double paidAmount;
    private LocalDateTime paidAt;
    private LocalDateTime requestedAt;
    private String vendorName;
    private boolean isPaid;

    public PayoutResponse(Payout payout) {
        this.id = payout.getId();
        this.paidAmount = payout.getPayoutAmount();
        this.isPaid = payout.isPaid();
        this.vendorName = payout.getShop().getUser().getFirstName() + " " + payout.getShop().getUser().getLastName();
        this.paidAt = payout.getPaidAt();
        this.requestedAt = payout.getCreatedAt();
    }


}

