/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.controller.DisputeController
 *  dev.gagnon.dto.request.AddDisputeRequest
 *  dev.gagnon.dto.response.DisputeResponse
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.service.DisputeService
 *  lombok.Generated
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.PutMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package dev.gagnon.controller;

import dev.gagnon.dto.request.AddDisputeRequest;
import dev.gagnon.dto.response.DisputeResponse;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.service.DisputeService;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispute")
@AllArgsConstructor
public class DisputeController {
    private final DisputeService disputeService;

    @PostMapping(value={"/add"}, consumes={"multipart/form-data"})
    public ResponseEntity<?> addDispute(@ModelAttribute AddDisputeRequest request) {
        try {
            String response = this.disputeService.add(request);
            return new ResponseEntity<>((Object)response, (HttpStatusCode)HttpStatus.CREATED);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>((Object)ex.getMessage(), (HttpStatusCode)HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<?> getDisputeById(@RequestParam(value="id") Long id) {
        try {
            DisputeResponse response = this.disputeService.getById(id);
            return new ResponseEntity<>((Object)response, (HttpStatusCode)HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>((Object)ex.getMessage(), (HttpStatusCode)HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/process")
    public ResponseEntity<?> processDispute(@RequestParam Long disputeId) {
        try {
            String response = this.disputeService.process(disputeId);
            return new ResponseEntity<>((Object)response, (HttpStatusCode)HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>((Object)ex.getMessage(), (HttpStatusCode)HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectDispute(@RequestParam Long disputeId) {
        try {
            String response = this.disputeService.reject(disputeId);
            return new ResponseEntity<>((Object)response, (HttpStatusCode)HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>((Object)ex.getMessage(), (HttpStatusCode)HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/send-resolve")
    public ResponseEntity<?> sendResolveRequest(@RequestParam Long disputeId) {
        try {
            String response = this.disputeService.sendResolveRequest(disputeId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        try {
            List<DisputeResponse> response = this.disputeService.getByUser(email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/resolve")
    public ResponseEntity<?> resolve(@RequestParam Long disputeId) {
        try {
            String response = this.disputeService.resolve(disputeId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-shop-disputes")
    public ResponseEntity<?> getShopDisputes(@RequestParam Long id) {
        try {
            List<DisputeResponse> userDisputes = this.disputeService.getByShop(id);
            return new ResponseEntity<>(userDisputes, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDisputes() {
        try {
            List<DisputeResponse> response = this.disputeService.getAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/allPending")
    public ResponseEntity<?> getAllPendingDisputes() {
        try {
            List<DisputeResponse> response = this.disputeService.getAllPending();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/allPendingCount")
    public ResponseEntity<?> getAllPendingDisputesCount() {
        try {
            Long response = this.disputeService.getAllPendingCount();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}

