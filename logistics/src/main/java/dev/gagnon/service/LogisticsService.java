package dev.gagnon.service;

import dev.gagnon.data.model.LogisticsCompany;
import dev.gagnon.data.model.Vehicle;
import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;

import java.util.List;

public interface LogisticsService {
    AddCompanyResponse onboardCompany(AddCompanyRequest request);
    AddVehicleResponse onboardVehicle(AddVehicleRequest request);
    AccountDetailsResponse addAccountDetails(AccountDetailsRequest request);
    OrderResponse scheduleDelivery(ScheduleDeliveryRequest request);
    Vehicle getVehicle(Long id);

    RiderResponse onboardRider(RiderRequest request);

    String beginTrip(RideRequest request);
    String pickUp(RideRequest request);

    String deliver(RideRequest request);

    UpdateFleetResponse updateFleet(UpdateFleetRequest request);

    List<VehicleResponse> getAllVehicles();

    List<RiderResponse> getAllRiders();

    Long getCompanyVehicleCount(String email);

    Long getAllCompanyTrucksCount(String email);

    Long getAllCompanyBikesCount(String email);

    String updateVehicleStatus(Long vehicleId, String status);

    List<VehicleResponse> getAvailableRiders();

    List<LogisticsCompany> getAllCompanies();

    String approvePartner(Long partnerId);

    String rejectPartner(Long partnerId);

    String deletePartner(Long partnerId);

    Object getCompanyStats();

    CompanyResponse getCompanyByOwnerEmail(String ownerEmail);

    boolean existsByOwnerEmail(String ownerEmail);

    List<DeliveryOrderResponse> getCompanyOrders(String vendorEmail);

    DeliveryOrderResponse createOrder(Long orderId);

    String acceptRide(Long orderId, String riderEmail);

    List<RiderResponse> getAllCompanyRiders(String email);

    Long getAllCompanyRidersCount(String email);

    List<VehicleResponse> getAllCompanyVehicles(String email);

    String removeRider(Long vehicleId);

    String removeCompanyRider(String email, Long riderId);

    List<VehicleResponse> getAllVehiclesForCompany(String email);


    AddVehicleResponse editVehicle(Long vehicleId, UpdateVehicleRequest request);

    String deleteVehicle(Long vehicleId);

    String deleteRider(Long riderId);

    List<DeliveryOrderResponse> getAllCompanyDeliveries(String email);
}
