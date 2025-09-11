package dev.gagnon.service.Implementation;

import dev.gagnon.dto.request.RequestRefundRequest;
import dev.gagnon.dto.response.RefundResponse;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.Order;
import dev.gagnon.model.Refund;
import dev.gagnon.model.Shop;
import dev.gagnon.model.User;
import dev.gagnon.model.constants.RefundStatus;
import dev.gagnon.repository.OrderRepository;
import dev.gagnon.repository.RefundRepository;
import dev.gagnon.repository.ShopRepository;
import dev.gagnon.repository.UserRepository;
import dev.gagnon.service.RefundService;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    @Override
    public String requestRefund(RequestRefundRequest request) {
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(
                () -> new ResourceNotFoundException("Order not found")
        );
     Refund refund = modelMapper.map(request, Refund.class);
     refund.setRefundAmount(order.getTotalAmount());
     refund.setStatus(RefundStatus.PENDING);
     refund.setOrderNumber(order.getOrderNumber());
     User buyer = userRepository.findByEmail(order.getBuyerEmail())
             .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));
     Shop shop = shopRepository.findById(order.getShopId())
                     .orElseThrow(()-> new ResourceNotFoundException("Shop not found"));
     refund.setBuyerName(buyer.getFirstName() + " " + buyer.getLastName());
     refund.setShopId(order.getShopId());
     refund.setVendorName(shop.getUser().getFirstName() + " " + shop.getUser().getLastName());
     refundRepository.save(refund);
       return "refund requested";
    }

    @Override
    public String process(Long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("refund not found"));
        refund.setStatus(RefundStatus.PROCESSED);
        refund.setProcessedAt(now());
        refundRepository.save(refund);
        return "refund processed";
    }

    @Override
    public List<RefundResponse> getAll() {
        List<Refund> refunds = refundRepository.findAll();
        if(refunds.isEmpty()) return List.of();
        return refunds
                .stream()
                .map(RefundResponse::new)
                .toList();
    }

    @Override
    public List<RefundResponse> getAllPendingRefunds() {
        List<Refund> refunds = refundRepository.findAllPending(RefundStatus.PENDING);
        if(refunds.isEmpty()) return List.of();
        return refunds
                .stream()
                .map(RefundResponse::new)
                .toList();
    }

    @Override
    public List<RefundResponse> getAllProcessedRefunds() {
        List<Refund> refunds = refundRepository.findAllPending(RefundStatus.PROCESSED);
        if(refunds.isEmpty()) return List.of();
        return refunds
                .stream()
                .map(RefundResponse::new)
                .toList();
    }

    @Override
    public RefundResponse getById(Long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("refund not found"));
        return new RefundResponse(refund);
    }

    @Override
    public Double getTotalProcessedAmount() {
        List<Refund> refunds = refundRepository.findAllPending(RefundStatus.PROCESSED);
        if(refunds.isEmpty()) return 0.0;
        return refunds.stream()
                .mapToDouble(refund -> refund.getRefundAmount().doubleValue())
                .sum();
    }

}

