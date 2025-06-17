package dev.gagnon.service.impls;

import com.cloudinary.Cloudinary;
import dev.gagnon.data.constants.Type;
import dev.gagnon.data.model.*;
import dev.gagnon.data.repository.DeliveryRepository;
import dev.gagnon.data.repository.LogisticsCompanyRepository;
import dev.gagnon.data.repository.RiderRepository;
import dev.gagnon.data.repository.VehicleRepository;
import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exceptions.CompanyExistsException;
import dev.gagnon.exceptions.LogisticsBaseException;
import dev.gagnon.service.LogisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

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
    private final RiderRepository riders;

    @Override
    public AddCompanyResponse onboardCompany(AddCompanyRequest request) {
        validateExistingCompany(request.getOwnerEmail());
        LogisticsCompany company = modelMapper.map(request, LogisticsCompany.class);
        String logoUrl = getMediaUrl(request.getLogo(), cloudinary.uploader());
        String cacUrl = getMediaUrl(request.getCacImage(), cloudinary.uploader());

        for (MultipartFile mediaFile : request.getOtherDocuments()) {
            String mediaUrl = getMediaUrl(mediaFile, cloudinary.uploader());
            company.getOtherDocumentUrls().add(mediaUrl);
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
        Rider rider = riders.findById(request.getRiderId())
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
        Vehicle vehicle = getVehicle(request.getVehicleId());
        Rider rider = modelMapper.map(request, Rider.class);
        rider.setVehicle(vehicle);
        riders.save(rider);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("email", request.getRiderEmail());
        formData.add("surname", request.getRiderFirstName());
        formData.add("otherName", request.getRiderLastName());
        formData.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://user-service/api/users/register",
                httpEntity,
                String.class
        );

//        UpdateRoleRequest roleRequest =UpdateRoleRequest
//                .builder()
//                .email(request.getRiderEmail())
//                .name("RIDER")
//                .build();
//        restTemplate.postForEntity(
//                "https://user-service/api/users/add-role",
//                roleRequest,
//                String.class
//
//        );
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
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream().map(vehicle -> modelMapper.map(
                vehicle, VehicleResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RiderResponse> getAllRiders() {
        List<Rider> allRiders = riders.findAll();

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
        List<Vehicle> allCompanyTrucks = vehicleRepository.findAllTrucksByOwnerEmail(email);
        return (long) allCompanyTrucks.size();
    }

    @Override
    public Long getAllCompanyBikesCount(String email) {
        List<Vehicle> allCompanyBikes = vehicleRepository.findAllBikesByOwnerEmail(email);
        return (long) allCompanyBikes.size();
    }

}
