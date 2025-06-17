package dev.gagnon.controller;

import dev.gagnon.data.model.LogisticsCompany;
import dev.gagnon.data.model.Rider;
import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exceptions.LogisticsBaseException;
import dev.gagnon.service.LogisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/logistics")
@AllArgsConstructor
public class LogisticsController {
    private final LogisticsService logisticsService;

    @PostMapping(path = "/onboardCompany", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> onboardCompany(@ModelAttribute AddCompanyRequest request) {
        try{
            AddCompanyResponse response = logisticsService.onboardCompany(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/onboardVehicle")
    public ResponseEntity<?> onboardVehicle(@RequestBody AddVehicleRequest request) {
        try{
            AddVehicleResponse response = logisticsService.onboardVehicle(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (LogisticsBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateFleetNumber")
    public ResponseEntity<?> updateFleetNumber(@RequestBody UpdateFleetRequest request) {
        try{
            UpdateFleetResponse response = logisticsService.updateFleet(request);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/addAccountDetails")
    public ResponseEntity<?> addAccountDetails(@RequestBody AccountDetailsRequest request) {
        try{
            AccountDetailsResponse response = logisticsService.addAccountDetails(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (LogisticsBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/scheduleDelivery")
    public ResponseEntity<?> scheduleDelivery(@RequestBody ScheduleDeliveryRequest request){
         OrderResponse response = logisticsService.scheduleDelivery(request);
         return new ResponseEntity<>(response, OK);
    }

    @PostMapping("/onboard-rider")
    public ResponseEntity<?> onboardRider(@RequestBody RiderRequest request){
        RiderResponse response = logisticsService.onboardRider(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/allVehicles")
    public ResponseEntity<?> getAllVehicles(){
        List<VehicleResponse> allVehicles = logisticsService.getAllVehicles();
        return new ResponseEntity<>(allVehicles,OK);
    }

    @GetMapping("/allCompanyVehicles")
    public ResponseEntity<?> getAllCompanyVehicles(){
        List<VehicleResponse> allVehicles = logisticsService.getAllVehicles();
        return new ResponseEntity<>(allVehicles,OK);
    }
    @GetMapping("/allCompanyBikesCount")
    public ResponseEntity<?> getAllCompanyBikesCount(Principal principal){
        String email = principal.getName();
        Long allCompanyBikesCount = logisticsService.getAllCompanyBikesCount(email);
        return new ResponseEntity<>(allCompanyBikesCount,OK);
    }

    @GetMapping("/allCompanyTrucksCount")
    public ResponseEntity<?> getAllCompanyTrucksCount(Principal principal){
        String email = principal.getName();
        Long allCompanyTrucksCount = logisticsService.getAllCompanyTrucksCount(email);
        return new ResponseEntity<>(allCompanyTrucksCount,OK);
    }

    @GetMapping("/allCompanyVehiclesCount")
    public ResponseEntity<?> getAllCompanyVehiclesCount(Principal principal){
        String email = principal.getName();
        Long allVehiclesCount = logisticsService.getCompanyVehicleCount(email);
        return new ResponseEntity<>(allVehiclesCount,OK);
    }

    @GetMapping("/vehicleCount")
    public ResponseEntity<?> getAllVehicleCount(){
        List<VehicleResponse> allVehicles = logisticsService.getAllVehicles();
        Long noOfVehicles = (long) allVehicles.size();
        return new ResponseEntity<>(noOfVehicles,OK);
    }

    @GetMapping("/allRiders")
    public ResponseEntity<?> getAllRiders(){
        List<RiderResponse> allRiders = logisticsService.getAllRiders();
        return new ResponseEntity<>(allRiders,OK);
    }

    @GetMapping("/riderCount")
    public ResponseEntity<?> getAllRiderCount(){
        List<RiderResponse> allRiders = logisticsService.getAllRiders();
        Long noOfVehicles = (long) allRiders.size();
        return new ResponseEntity<>(noOfVehicles,OK);
    }

    @PostMapping("/begin-ride")
    public ResponseEntity<?>beginRide(@RequestBody RideRequest request){
        String response = logisticsService.beginTrip(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/pick-up")
    public ResponseEntity<?>pickUp(@RequestBody RideRequest request){
        String response = logisticsService.pickUp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deliver")
    public ResponseEntity<?>deliver(@RequestBody RideRequest request){
        String response = logisticsService.deliver(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
