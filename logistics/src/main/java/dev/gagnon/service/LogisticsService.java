package dev.gagnon.service;

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
}
