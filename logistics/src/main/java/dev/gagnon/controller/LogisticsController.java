package dev.gagnon.controller;

import dev.gagnon.data.model.LogisticsCompany;
import dev.gagnon.dto.request.*;
import dev.gagnon.dto.response.*;
import dev.gagnon.exceptions.LogisticsBaseException;
import dev.gagnon.service.LogisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static org.springframework.http.HttpStatus.*;
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
            return new ResponseEntity<>(response, CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam Long orderId){
        try{
            DeliveryOrderResponse response = logisticsService.createOrder(orderId);
            return new ResponseEntity<>(response, CREATED);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/getCompanyByOwnerEmail")
    public ResponseEntity<?> getCompanyByOwnerEmail(@RequestParam String ownerEmail) {
        try{
            CompanyResponse response = logisticsService.getCompanyByOwnerEmail(ownerEmail);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/existsByOwnerEmail")
    public ResponseEntity<?> existsByOwnerEmail(@RequestParam String ownerEmail) {
        try{
            boolean response = logisticsService.existsByOwnerEmail(ownerEmail);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PostMapping("/onboardVehicle")
    public ResponseEntity<?> onboardVehicle(@RequestBody AddVehicleRequest request) {
        try{
            AddVehicleResponse response = logisticsService.onboardVehicle(request);
            return new ResponseEntity<>(response, CREATED);
        }
        catch (LogisticsBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/editVehicle")
    public ResponseEntity<?> editVehicle(@RequestParam Long vehicleId,@RequestBody UpdateVehicleRequest request) {
        try{
            AddVehicleResponse response = logisticsService.editVehicle(vehicleId,request);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-vehicle")
    public ResponseEntity<?> deleteVehicle(@RequestParam Long vehicleId) {
        try{
            String response = logisticsService.deleteVehicle(vehicleId);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-rider")
    public ResponseEntity<?> deleteRider(@RequestParam Long riderId) {
        try{
            String response = logisticsService.deleteRider(riderId);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>(response, CREATED);
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
        return new ResponseEntity<>(response, CREATED);
    }

    @GetMapping("/allVehicles")
    public ResponseEntity<?> getAllVehicles(){
        List<VehicleResponse> allVehicles = logisticsService.getAllVehicles();
        return new ResponseEntity<>(allVehicles,OK);
    }

    @GetMapping("/allCompanyVehicles")
    public ResponseEntity<?> getAllCompanyVehicles(@RequestParam String email){
        List<VehicleResponse> allVehicles = logisticsService.getAllCompanyVehicles(email);
        return new ResponseEntity<>(allVehicles,OK);
    }

    @GetMapping("/allVehiclesForCompany")
    public ResponseEntity<?> getAllVehiclesForCompany(@RequestParam String email){
        List<VehicleResponse> allVehicles = logisticsService.getAllVehiclesForCompany(email);
        return new ResponseEntity<>(allVehicles,OK);
    }


    @GetMapping("/allCompanyBikesCount")
    public ResponseEntity<?> getAllCompanyBikesCount(@RequestParam String email){
        Long allCompanyBikesCount = logisticsService.getAllCompanyBikesCount(email);
        return new ResponseEntity<>(allCompanyBikesCount,OK);
    }

    @PutMapping("/remove-rider")
    public ResponseEntity<?>removeRider(@RequestParam Long vehicleId){
        try{
            String response =  logisticsService.removeRider(vehicleId);
            return new ResponseEntity<>(response, OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/allCompanyTrucksCount")
    public ResponseEntity<?> getAllCompanyTrucksCount(@RequestParam String email){
        Long allCompanyTrucksCount = logisticsService.getAllCompanyTrucksCount(email);
        return new ResponseEntity<>(allCompanyTrucksCount,OK);
    }

    @GetMapping("/allCompanyVehiclesCount")
    public ResponseEntity<?> getAllCompanyVehiclesCount(@RequestParam String email){
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

    @GetMapping("/allCompanyRiders")
    public ResponseEntity<?> getAllCompanyRiders(@RequestParam String email){
        List<RiderResponse> allRiders = logisticsService.getAllCompanyRiders(email);
        return new ResponseEntity<>(allRiders,OK);
    }

    @DeleteMapping("/remove-companyRider")
    public ResponseEntity<?> removeCompanyRider(@RequestParam String email, @RequestParam Long riderId){
        try{
            String response = logisticsService.removeCompanyRider(email,riderId);
            return new ResponseEntity<>(response, OK);
        }
        catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/allCompanyRidersCount")
    public ResponseEntity<?> getAllCompanyRidersCount(@RequestParam String email){
        Long allRiders = logisticsService.getAllCompanyRidersCount(email);
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

    @PutMapping("/update-vehicle-status")
    public ResponseEntity<?>updateVehicleStatus(@RequestParam Long vehicleId, @RequestParam String status){
        try{
            String response = logisticsService.updateVehicleStatus(vehicleId,status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/accept-ride")
    public ResponseEntity<?>acceptRide(@RequestParam Long orderId, @RequestParam String riderEmail){
        try{
            String response = logisticsService.acceptRide(orderId,riderEmail);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/get-available-riders")
    public ResponseEntity<?> getAvailableRiders(){
        try{
            List<VehicleResponse> response = logisticsService.getAvailableRiders();
            return new ResponseEntity<>(response,OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<?> getAllCompanies(){
        try{
            List<LogisticsCompany> companies = logisticsService.getAllCompanies();
            return new ResponseEntity<>(companies, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PostMapping("/approve-partner/{partnerId}")
    public ResponseEntity<?> approvePartner(@PathVariable Long partnerId){
        try{
            String response = logisticsService.approvePartner(partnerId);
            return new ResponseEntity<>(response, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PostMapping("/reject-partner/{partnerId}")
    public ResponseEntity<?> rejectPartner(@PathVariable Long partnerId){
        try{
            String response = logisticsService.rejectPartner(partnerId);
            return new ResponseEntity<>(response, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @DeleteMapping("/partner/{partnerId}")
    public ResponseEntity<?> deletePartner(@PathVariable Long partnerId){
        try{
            String response = logisticsService.deletePartner(partnerId);
            return new ResponseEntity<>(response, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/companies/stats")
    public ResponseEntity<?> getCompanyStats(){
        try{
            // Return company statistics
            var stats = logisticsService.getCompanyStats();
            return new ResponseEntity<>(stats, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/company-orders")
    public ResponseEntity<?> getCompanyOrders(@RequestParam String email){
        try{
            List<DeliveryOrderResponse> response = logisticsService.getCompanyOrders(email);
            return new ResponseEntity<>(response, OK);
        }catch (LogisticsBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }


}
