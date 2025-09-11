/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cloudinary.Cloudinary
 *  com.cloudinary.Uploader
 *  dev.gagnon.dto.request.AddDisputeRequest
 *  dev.gagnon.dto.request.NotificationEvent
 *  dev.gagnon.dto.response.DisputeResponse
 *  dev.gagnon.exception.BusinessException
 *  dev.gagnon.exception.ResourceNotFoundException
 *  dev.gagnon.model.Dispute
 *  dev.gagnon.model.Order
 *  dev.gagnon.model.OrderItem
 *  dev.gagnon.model.User
 *  dev.gagnon.model.constants.DisputeStatus
 *  dev.gagnon.repository.DisputeRepository
 *  dev.gagnon.repository.OrderItemRepository
 *  dev.gagnon.repository.OrderRepository
 *  dev.gagnon.repository.UserRepository
 *  dev.gagnon.service.DisputeService
 *  dev.gagnon.service.ServiceClient
 *  dev.gagnon.utils.ServiceUtils
 *  lombok.Generated
 *  org.springframework.stereotype.Service
 *  org.springframework.web.multipart.MultipartFile
 */
package dev.gagnon.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import dev.gagnon.dto.request.AddDisputeRequest;
import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.response.DisputeResponse;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import dev.gagnon.model.Dispute;
import dev.gagnon.model.Order;
import dev.gagnon.model.OrderItem;
import dev.gagnon.model.User;
import dev.gagnon.model.constants.DisputeStatus;
import dev.gagnon.repository.DisputeRepository;
import dev.gagnon.repository.OrderItemRepository;
import dev.gagnon.repository.OrderRepository;
import dev.gagnon.repository.UserRepository;
import dev.gagnon.utils.ServiceUtils;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DisputeService {
    private final DisputeRepository disputeRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ServiceClient serviceClient;
    private final Cloudinary cloudinary;

    public String add(AddDisputeRequest request) {
        List<Order> orders = this.orderRepository.findByOrderNumber(request.getOrderNumber());
        Optional<Order> disputeOrderOptional = orders.stream().filter(order -> order.getItems().stream().anyMatch(item -> Objects.equals(item.getId(), request.getItemId()))).findFirst();
        Order disputeOrder = disputeOrderOptional.orElse(null);
        if (disputeOrder == null) {
            throw new BusinessException("Item is not part of the order");
        }
        Dispute existing = this.disputeRepository.findByItemId(request.getItemId());
        if (existing != null) {
            throw new BusinessException("Item has already been added");
        }
        OrderItem orderItem = orderItemRepository.findById(request.getItemId()).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        Dispute dispute = new Dispute();
        String disputeImageUrl = ServiceUtils.getMediaUrl((MultipartFile)request.getItemImage(), (Uploader)this.cloudinary.uploader());
        dispute.setImageUrl(disputeImageUrl);
        dispute.setStatus(DisputeStatus.PENDING);
        dispute.setReason(request.getReason());
        dispute.setOrderNumber(String.valueOf(request.getOrderNumber()));
        dispute.setItem(orderItem);
        this.disputeRepository.save(dispute);
        NotificationEvent event = new NotificationEvent();
        event.setTitle("Dispute Initiated");
        event.setType("INFO");
        event.setEmail(dispute.getItem().getShop().getUser().getEmail());
        event.setMessage("Dispute initiated for the order " + request.getOrderNumber());
        this.serviceClient.sendNotification(event);
        event.setEmail(dispute.getItem().getOrder().getBuyerEmail());
        event.setMessage(" Your dispute has been initiated for the order " + request.getOrderNumber());
        this.serviceClient.sendNotification(event);
        return "Dispute created";
    }

    public DisputeResponse getById(Long id) {
        Dispute dispute = (Dispute)this.disputeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        User user = userRepository.findByEmail(dispute.getItem().getOrder().getBuyerEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        DisputeResponse response = new DisputeResponse(dispute);
        response.getOrderItem().setBuyerName(user.getFirstName() + " " + user.getLastName());
        return response;
    }

    public String process(Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        dispute.setStatus(DisputeStatus.PROCESSING);
        this.disputeRepository.save(dispute);
        NotificationEvent event = new NotificationEvent();
        event.setTitle("Dispute In Process");
        event.setType("INFO");
        event.setEmail(dispute.getItem().getOrder().getBuyerEmail());
        event.setMessage("Your dispute is in process for the order " + dispute.getOrderNumber());
        this.serviceClient.sendNotification(event);
        return "Dispute in process";
    }

    public String reject(Long disputeId) {
        Dispute dispute = this.disputeRepository.findById(disputeId).orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        dispute.setStatus(DisputeStatus.REJECTED);
        this.disputeRepository.save(dispute);
        NotificationEvent event = new NotificationEvent();
        event.setTitle("Dispute Rejected");
        event.setType("INFO");
        event.setEmail(dispute.getItem().getOrder().getBuyerEmail());
        event.setMessage("Your dispute has been rejected for the order " + dispute.getOrderNumber());
        this.serviceClient.sendNotification(event);
        return "Dispute rejected";
    }

    public String sendResolveRequest(Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        dispute.setStatus(DisputeStatus.PENDING_RESOLUTION);
        this.disputeRepository.save(dispute);
        NotificationEvent event = new NotificationEvent();
        event.setTitle("Dispute Pending approval");
        event.setType("INFO");
        event.setEmail(dispute.getItem().getOrder().getBuyerEmail());
        event.setMessage("Your dispute is resolved and pending approval for the order " + dispute.getOrderNumber());
        this.serviceClient.sendNotification(event);
        return "Dispute pending resolution approval";
    }

    public String resolve(Long disputeId) {
        Dispute dispute = (Dispute)this.disputeRepository.findById(disputeId).orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        dispute.setStatus(DisputeStatus.RESOLVED);
        this.disputeRepository.save(dispute);
        NotificationEvent event = new NotificationEvent();
        event.setTitle("Dispute Resolved");
        event.setType("INFO");
        event.setEmail(dispute.getItem().getOrder().getBuyerEmail());
        event.setMessage("Your dispute has been resolved for the order " + dispute.getOrderNumber());
        this.serviceClient.sendNotification(event);
        event.setEmail(dispute.getItem().getShop().getUser().getEmail());
        event.setMessage("Dispute has been resolved for the order " + dispute.getOrderNumber());
        return "Dispute resolved";
    }

    public List<DisputeResponse> getByShop(Long id) {
        List<Dispute> disputes = this.disputeRepository.findByShop(id);
        return this.getDisputeResponses(disputes);
    }

    public List<DisputeResponse> getAll() {
        List disputes = this.disputeRepository.findAll();
        return this.getDisputeResponses(disputes);
    }

    private List<DisputeResponse> getDisputeResponses(List<Dispute> disputes) {
        return disputes.stream().map(dispute -> {
            DisputeResponse disputeResponse = new DisputeResponse(dispute);
            User user = userRepository.findByEmail(dispute.getItem().getOrder().getBuyerEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            disputeResponse.getOrderItem().setBuyerName(user.getFirstName() + " " + user.getLastName());
            return disputeResponse;
        }).toList();
    }

    public List<DisputeResponse> getByUser(String email) {
        List<Dispute> disputes = this.disputeRepository.findByEmail(email);
        if (disputes.isEmpty()) {
            return List.of();
        }
        return this.getDisputeResponses(disputes);
    }

    public List<DisputeResponse> getAllPending() {
        List<Dispute> disputes = this.disputeRepository.findAllPending(DisputeStatus.PENDING);
        return this.getDisputeResponses(disputes);
    }

    public Long getAllPendingCount() {
        List<Dispute> disputes = disputeRepository.findAllPending(DisputeStatus.PENDING);
        return (long) disputes.size();
    }

    @Generated
    public DisputeService(DisputeRepository disputeRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository, ServiceClient serviceClient, Cloudinary cloudinary) {
        this.disputeRepository = disputeRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.serviceClient = serviceClient;
        this.cloudinary = cloudinary;
    }
}

