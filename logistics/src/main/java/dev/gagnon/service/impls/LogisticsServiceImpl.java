package dev.gagnon.service.impls;

import com.cloudinary.Cloudinary;
import dev.gagnon.data.constants.RideCurrentStatus;
import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.*;
import dev.gagnon.data.repository.*;
import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exceptions.CompanyExistsException;
import dev.gagnon.exceptions.LogisticsBaseException;
import dev.gagnon.exceptions.ResourceNotFoundException;
import dev.gagnon.service.LogisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.gagnon.data.constants.RideCurrentStatus.AVAILABLE;
import static dev.gagnon.data.constants.Type.BIKE;
import static dev.gagnon.utils.ServiceUtils.getMediaUrl;

@Slf4j
@Service
@AllArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {
    private final ModelMapper modelMapper;
    private final LogisticsCompanyRepository companyRepository;
    private final Cloudinary cloudinary;
    private final RestTemplate restTemplate;
    private final VehicleRepository vehicleRepository;
    private final DeliveryRepository deliveryRepository;
    private final LogisticsCompanyRepository logisticsCompanyRepository;
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;

    @Override
    public AddCompanyResponse onboardCompany(AddCompanyRequest request) {
        // Validate existing company
        validateExistingCompany(request.getOwnerEmail());
        // Map request to entity
        LogisticsCompany company = modelMapper.map(request, LogisticsCompany.class);
        // Handle logo (optional)
        String logoUrl = null;
        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            logoUrl = getMediaUrl(request.getLogo(), cloudinary.uploader());
        }
        String cacUrl = getMediaUrl(request.getCacImage(), cloudinary.uploader());
        if (request.getOtherDocuments() != null && !request.getOtherDocuments().isEmpty()) {
            for (MultipartFile mediaFile : request.getOtherDocuments()) {
                if (mediaFile != null && !mediaFile.isEmpty()) {
                    String mediaUrl = getMediaUrl(mediaFile, cloudinary.uploader());
                    company.getOtherDocumentUrls().add(mediaUrl);
                }
            }
        }
        company.setLogoUrl(logoUrl);
        company.setCacImageUrl(cacUrl);
        companyRepository.save(company);
        return modelMapper.map(company, AddCompanyResponse.class);
    }

    private void validateExistingCompany(String ownerEmail) {
        boolean existsByOwnerName = companyRepository.existsByOwnerEmail(ownerEmail);
        if (existsByOwnerName)throw new CompanyExistsException("company already created by user");
    }


    @Override
    public AddVehicleResponse onboardVehicle(AddVehicleRequest request) {
        LogisticsCompany company = companyRepository.findByOwnerEmail(request.getOwnerEmail()).get();
        Vehicle vehicle = modelMapper.map(request, Vehicle.class);
        vehicle.setType(Type.valueOf(request.getType()));
        vehicle.setStatus(RideCurrentStatus.UNAVAILABLE);
        vehicle.setCompany(company);
        vehicleRepository.save(vehicle);
        return modelMapper.map(company, AddVehicleResponse.class);
    }

    @Override
    public AccountDetailsResponse addAccountDetails(AccountDetailsRequest request) {
        BankDetails bankDetails = modelMapper.map(request, BankDetails.class);
        LogisticsCompany company = companyRepository.findById(request.getCompanyId()).get();
        bankDetails.setCompany(company);
        return modelMapper.map(bankDetails, AccountDetailsResponse.class);
    }

    @Override
    public OrderResponse scheduleDelivery(ScheduleDeliveryRequest request) {
        ResponseEntity<OrderResponse> response= restTemplate.getForEntity(
                "http://order-service/api/orders/{orderNumber}",
                OrderResponse.class,
                request.getOrderNumber());
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RuntimeException("Rider not found"));
        DeliveryOrder order = new DeliveryOrder();
        order.setRider(rider);
        deliveryRepository.save(order);

        log.info("Order: {}", response.getBody());
        return response.getBody();
    }

    @Override
    public Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @Override
    public RiderResponse onboardRider(RiderRequest request) {

        // Prepare form data for user-service registration
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("email", request.getRiderEmail());
        formData.add("firstName", request.getRiderFirstName());
        formData.add("lastName", request.getRiderLastName());
        formData.add("password", "password");
        formData.add("phone", request.getPhone());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(formData, headers);
        // Call user-service to register rider
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://user-service/api/users/register-rider",
                httpEntity,
                String.class
        );

        // Check if the registration was successful
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new LogisticsBaseException("Failed to register rider: " + response.getBody());
        }
        // Check if rider already exists by email
        boolean existsByEmail = riderRepository.existsByEmail(request.getRiderEmail());
        if (existsByEmail) {
            throw new LogisticsBaseException("Rider already exists by email");
        }
        Vehicle vehicle = getVehicle(request.getVehicleId());
        if (vehicle.getRider() != null) {
            throw new LogisticsBaseException("Vehicle already assigned to a rider!");
        }

        // Map request to Rider entity
        Rider rider = modelMapper.map(request, Rider.class);
        rider.setPhone(request.getPhone());
        rider.setCompany(vehicle.getCompany());
        // Save rider only after successful registration
        Rider savedRider = riderRepository.saveAndFlush(rider);

        vehicle.setRider(savedRider);
        vehicleRepository.save(vehicle);
        return modelMapper.map(rider, RiderResponse.class);
    }
    @Override
    public String beginTrip(RideRequest request) {
        DeliveryOrder order = deliveryRepository.findById(request.getOrderId()).get();
        order.setOrderStatus(OrderStatus.IN_TRANSIT);
        deliveryRepository.save(order);
        return "successfully began the trip";
    }

    @Override
    public String pickUp(RideRequest request) {
        DeliveryOrder order = deliveryRepository.findById(request.getOrderId()).get();
        order.setOrderStatus(OrderStatus.PICKED_UP);
        deliveryRepository.save(order);
        return "successfully picked up the product";
    }

    @Override
    public String deliver(RideRequest request) {
        DeliveryOrder order = deliveryRepository.findById(request.getOrderId()).get();
        order.setOrderStatus(OrderStatus.PICKED_UP);
        deliveryRepository.save(order);
        return "successfully delivered, awaiting approval";
    }

    @Override
    public UpdateFleetResponse updateFleet(UpdateFleetRequest request) {
        LogisticsCompany logisticsCompany = companyRepository.findByOwnerEmail(request.getOwnerEmail())
                .orElseThrow(()->new LogisticsBaseException("company not found"));
        logisticsCompany.setFleetNumber((long) request.getFleetNumber());
        companyRepository.save(logisticsCompany);
        return modelMapper.map(logisticsCompany, UpdateFleetResponse.class);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            return vehicles.stream()
                    .filter(vehicle -> vehicle.getType() != null) // Filter out vehicles with null type
                    .map(vehicle -> modelMapper.map(vehicle, VehicleResponse.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent service failure
            System.err.println("Error fetching vehicles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<RiderResponse> getAllRiders() {
        List<Rider> allRiders = riderRepository.findAll();
        return allRiders.stream().map(vehicle -> modelMapper.map(
                        vehicle, RiderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long getCompanyVehicleCount(String email) {
        List<Vehicle> allCompanyVehicles = vehicleRepository.findAllByOwnerEmail(email);
        return (long) allCompanyVehicles.size();
    }

    @Override
    public Long getAllCompanyTrucksCount(String email) {
        List<Vehicle> allCompanyTrucks = vehicleRepository.findAllTrucksByOwnerEmail(email,Type.TRUCK);
        return (long) allCompanyTrucks.size();
    }

    @Override
    public Long getAllCompanyBikesCount(String email) {
        List<Vehicle> allCompanyBikes = vehicleRepository.findAllBikesByOwnerEmail(email,BIKE);
        return (long) allCompanyBikes.size();
    }

    @Override
    public String updateVehicleStatus(Long vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicle.setStatus(RideCurrentStatus.valueOf(status));
        vehicleRepository.save(vehicle);
        return "status updated to "+vehicle.getStatus();
    }

    @Override
    public List<VehicleResponse> getAvailableRiders() {
        List<Vehicle> vehicles = vehicleRepository.findAllAvailable(AVAILABLE);
        if(vehicles.isEmpty())return List.of();
        return vehicles.stream()
                .map(vehicle->modelMapper.map(vehicle, VehicleResponse.class))
                .toList();
    }

    @Override
    public List<LogisticsCompany> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public String approvePartner(Long partnerId) {
        LogisticsCompany company = companyRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
        // Add status field to LogisticsCompany model if not exists
        // company.setStatus("ACTIVE");
        // companyRepository.save(company);
        return "Partner approved successfully";
    }

    @Override
    public String rejectPartner(Long partnerId) {
        LogisticsCompany company = companyRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
        // Add status field to LogisticsCompany model if not exists
        // company.setStatus("REJECTED");
        // companyRepository.save(company);
        return "Partner rejected successfully";
    }

    @Override
    public String deletePartner(Long partnerId) {
        LogisticsCompany company = companyRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
        companyRepository.delete(company);
        return "Partner deleted successfully";
    }

    @Override
    public Object getCompanyStats() {
        long totalCompanies = companyRepository.count();
        // For now, return basic stats. You can enhance this with actual status-based counts
        return new Object() {
            public final long totalPartners = totalCompanies;
            public final long activePartners = totalCompanies * 3 / 4; // Mock calculation
            public final long inactivePartners = totalCompanies / 8; // Mock calculation
            public final long pendingPartners = totalCompanies / 4; // Mock calculation
        };
    }

    @Override
    public CompanyResponse getCompanyByOwnerEmail(String ownerEmail) {
        LogisticsCompany company = logisticsCompanyRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return modelMapper.map(company,CompanyResponse.class);
    }

    @Override
    public boolean existsByOwnerEmail(String ownerEmail) {
        return logisticsCompanyRepository.existsByOwnerEmail(ownerEmail);
    }

    @Override
    public List<DeliveryOrderResponse> getCompanyOrders(String vendorEmail) {
        List<DeliveryOrder> orders = deliveryRepository.findByCompany(vendorEmail);
        if(orders.isEmpty())return List.of();
        return orders.stream()
                .map(this::mapToDeliveryOrderResponse)
                .toList();
    }

    @Override
    public DeliveryOrderResponse createOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderNumber(order.getOrderNumber());
        deliveryOrder.setOrderStatus(OrderStatus.PENDING);
        deliveryOrder.setDeliveryFee(order.getDeliveryFee());
        deliveryOrder.setTotalAmount(order.getTotalAmount());
        deliveryOrder.setCustomerName(order.getCustomerName());
        deliveryOrder.setDeliveryInfo(order.getDeliveryInfo());
        deliveryOrder.setGrandTotal(order.getGrandTotal());
        deliveryRepository.save(deliveryOrder);
        return mapToDeliveryOrderResponse(deliveryOrder);
    }

    @Override
    public String acceptRide(Long orderId, String riderEmail) {
        DeliveryOrder order = deliveryRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("order not found"));
        Rider rider = riderRepository.findByEmail(riderEmail);
        order.setRider(rider);
        order.setOrderStatus(OrderStatus.ACCEPTED);
        deliveryRepository.save(order);
        return "order accepted";
    }

    @Override
    public List<RiderResponse> getAllCompanyRiders(String email) {
        List<Rider> allRiders = riderRepository.findAllByCompany(email);
        if(allRiders.isEmpty())return List.of();
        return allRiders.stream()
                .map(RiderResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Long getAllCompanyRidersCount(String email) {
        List<Rider> allRiders = riderRepository.findAllByCompany(email);
        if(allRiders.isEmpty())return 0L;
        return (long) allRiders.size();
    }

    @Override
    public List<VehicleResponse> getAllCompanyVehicles(String email) {
        List<Vehicle> vehicles = vehicleRepository.findAllByOwnerEmail(email);
        if (vehicles.isEmpty())return List.of();
        return vehicles.stream()
                .filter(vehicle -> vehicle.getType() != null && vehicle.getRider()==null)
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public String removeRider(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if(vehicle.getRider()==null)
            throw new LogisticsBaseException("no rider found");
        vehicle.setRider(null);
        vehicleRepository.save(vehicle);
        return "rider removed";
    }

    @Override
    public String removeCompanyRider(String email, Long riderId) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("rider not found"));

        // First delete from rider repository
        riderRepository.delete(rider);

        try {
            // Build URL with query parameter - use rider's email from the entity
            String url = "http://user-service/api/users/delete-by-email?email=" +
                    URLEncoder.encode(rider.getEmail(), StandardCharsets.UTF_8);

            // Call user-service to delete user by email
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null, // No request body for DELETE with query params
                    String.class
            );

            // Check if the deletion was successful
            if (response.getStatusCode() != HttpStatus.OK) {
                // Log the error but don't throw exception since rider is already deleted
                log.warn("Failed to delete user from user-service: {}", response.getBody());
                return "rider deleted but user-service cleanup failed: " + response.getBody();
            }

            return "rider and user deleted successfully";

        } catch (Exception e) {
            // Handle exceptions but don't throw since rider is already deleted
            log.error("Error calling user-service delete endpoint: {}", e.getMessage());
            return "rider deleted but user-service cleanup failed: " + e.getMessage();
        }
    }

    @Override
    public List<VehicleResponse> getAllVehiclesForCompany(String email) {
        List<Vehicle> vehicles = vehicleRepository.findAllByOwnerEmail(email);
        if (vehicles.isEmpty())return List.of();
        return vehicles.stream()
                .filter(vehicle -> vehicle.getType() != null)
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public AddVehicleResponse editVehicle(Long vehicleId, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found"));
        vehicle.setType(Type.valueOf(request.getType()));
        vehicle.setEngineNumber(request.getEngineNumber());
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicleRepository.save(vehicle);
        return modelMapper.map(vehicle, AddVehicleResponse.class);
    }

    @Override
    public String deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found"));
        vehicleRepository.delete(vehicle);
        return "vehicle deleted successfully";
    }

    @Override
    public String deleteRider(Long riderId) {
        // Check if rider exists
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found with id: " + riderId));

        log.info("Rider email: '{}'", rider.getEmail());

        // Unassign vehicles from rider
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (!vehicles.isEmpty()) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getRider() != null) {
                    vehicle.setRider(null);
                    vehicleRepository.save(vehicle);
                }
            }
        }

        try {
            // Build URL safely with UriComponentsBuilder
            String url = UriComponentsBuilder
                    .fromHttpUrl("http://user-service/api/users/delete-by-email")
                    .queryParam("email", rider.getEmail())
                    .toUriString();

            // Call user-service to delete user by email
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("Failed to delete user from user-service: {}", response.getBody());
            }

        } catch (Exception e) {
            log.error("Error calling user-service delete endpoint: {}", e.getMessage());
        }

        // Finally delete rider locally
        riderRepository.delete(rider);
        return "Rider deleted successfully";
    }

    @Override
    public List<DeliveryOrderResponse> getAllCompanyDeliveries(String email) {
        List<DeliveryOrder> orders = deliveryRepository.findByCompany(email);
        return List.of();
    }


    private DeliveryOrderResponse mapToDeliveryOrderResponse(DeliveryOrder order) {
        return DeliveryOrderResponse
                .builder()
                .id(order.getId())
                .deliveryAddress(order.getDeliveryInfo().getAddress())
                .deliveryMethod(String.valueOf(order.getDeliveryInfo().getMethod()))
                .deliveryFee(order.getDeliveryFee().doubleValue())
                .customerName(order.getCustomerName())
                .orderNumber(order.getOrderNumber())
                .createdAt(order.getCreatedAt())
                .contactPhone(order.getDeliveryInfo().getContactPhone())
                .riderName(order.getRider().getFirstName())
                .riderPhone(order.getRider().getPhone())
                .build();
    }

}
