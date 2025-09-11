/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.NotificationEvent
 *  dev.gagnon.dto.request.PayoutRequest
 *  dev.gagnon.dto.request.ProcessPayoutRequest
 *  dev.gagnon.dto.response.PayoutResponse
 *  dev.gagnon.exception.BusinessException
 *  dev.gagnon.exception.ResourceNotFoundException
 *  dev.gagnon.model.Payout
 *  dev.gagnon.model.Shop
 *  dev.gagnon.repository.PayoutRepository
 *  dev.gagnon.repository.ShopRepository
 *  dev.gagnon.service.Implementation.PayoutServiceImpl
 *  dev.gagnon.service.PayoutService
 *  dev.gagnon.service.ServiceClient
 *  lombok.Generated
 *  org.springframework.stereotype.Service
 */
package dev.gagnon.service.Implementation;

import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.request.PayoutRequest;
import dev.gagnon.dto.request.ProcessPayoutRequest;
import dev.gagnon.dto.response.PayoutResponse;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.Payout;
import dev.gagnon.model.Shop;
import dev.gagnon.repository.PayoutRepository;
import dev.gagnon.repository.ShopRepository;
import dev.gagnon.service.PayoutService;
import dev.gagnon.service.ServiceClient;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PayoutServiceImpl
implements PayoutService {
    private final PayoutRepository payoutRepository;
    private final ShopRepository shopRepository;
    private final ServiceClient serviceClient;

    public String requestPayout(PayoutRequest request) {
        Shop shop = shopRepository.findById(request.getShopId()).orElseThrow(() -> new ResourceNotFoundException("shop not found"));
        if (request.getAmount() > shop.getTotalPayoutAmount().doubleValue()) {
            throw new BusinessException("amount less than payout amount");
        }
        if (request.getAmount() < 1000.0) {
            throw new BusinessException("amount less than minimum withdraw amount");
        }
        List<Payout> payouts = this.payoutRepository.findAllByShop(request.getShopId());
        double amount = payouts.stream().filter(payout -> !payout.isPaid()).mapToDouble(Payout::getPayoutAmount).sum();
        if (request.getAmount() < amount) {
            throw new BusinessException("You have pending payouts, new request amount less than payout amount");
        }
        Payout payout2 = new Payout();
        payout2.setPayoutAmount(request.getAmount());
        payout2.setShop(shop);
        payout2.setPaid(false);
        payout2.setAccountNumber(request.getAccountNumber());
        payout2.setBankName(request.getBankName());
        this.payoutRepository.save(payout2);
        NotificationEvent event = new NotificationEvent();
        event.setEmail("bdicadmin@gmail.com");
        event.setType("INFO");
        event.setTitle("Payout request");
        event.setMessage("Vendor has a payout request waiting ..");
        this.serviceClient.sendNotification(event);
        return "Payout requested successfully";
    }

    public String process(Long id, ProcessPayoutRequest request) {
        Shop shop = shopRepository.findById(request.getShopId()).orElseThrow(() -> new ResourceNotFoundException("shop not found"));
        Payout payout = (Payout)this.payoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("payout not found"));
        payout.setPaid(true);
        shop.setTotalPayoutAmount(BigDecimal.valueOf(shop.getTotalPayoutAmount().doubleValue() - request.getAmount()));
        shopRepository.save(shop);
        payoutRepository.save(payout);
        return "payout processed successfully";
    }

    public List<PayoutResponse> getAllPayout() {
        List<Payout> payouts = this.payoutRepository.findAll();
        if (payouts.isEmpty()) {
            return List.of();
        }
        return payouts.stream().map(PayoutResponse::new).collect(Collectors.toList());
    }

    public PayoutResponse getPayout(Long id) {
        Payout payout = (Payout)this.payoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("payout not found"));
        return new PayoutResponse(payout);
    }

    public List<PayoutResponse> getAllShopPayouts(Long shopId) {
        List<Payout> payouts = this.payoutRepository.findAllByShop(shopId);
        if (payouts.isEmpty()) {
            return List.of();
        }
        return payouts.stream().map(PayoutResponse::new).collect(Collectors.toList());
    }

    public PayoutResponse markAsPaid(Long id) {
        Payout payout = payoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("payout not found"));
        if (payout.isPaid()) {
            throw new BusinessException("Payout is already marked as paid");
        }
        payout.setPaid(true);
        payout.setPaidAt(LocalDateTime.now());
        this.payoutRepository.save(payout);
        return new PayoutResponse(payout);
    }


}

