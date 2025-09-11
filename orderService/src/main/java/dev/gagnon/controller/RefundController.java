package dev.gagnon.controller;

import dev.gagnon.dto.request.RequestRefundRequest;
import dev.gagnon.dto.response.RefundResponse;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.model.Refund;
import dev.gagnon.service.RefundService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/refund")
@AllArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @PostMapping("/request")
    public ResponseEntity<?>requestRefund(@RequestBody RequestRefundRequest request) {
        try{
            String response = refundService.requestRefund(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (OrderBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/process")
    public ResponseEntity<?>processRefund(@RequestParam Long id) {
        try{
            String response = refundService.process(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (OrderBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRefunds(){
        List<RefundResponse> response = refundService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getById")
    public ResponseEntity<?> getById(@RequestParam Long id){
        try {
            RefundResponse response = refundService.getById(id);
            return ResponseEntity.ok(response);
        }
        catch (OrderBaseException ex){
            return new ResponseEntity<>(ex.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingRefunds(){
        List<RefundResponse> response = refundService.getAllPendingRefunds();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-processed")
    public ResponseEntity<?> getAllProcessedRefunds(){
        List<RefundResponse> response = refundService.getAllProcessedRefunds();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-count")
    public ResponseEntity<?> getAllRefundsCount(){
        List<RefundResponse> response = refundService.getAll();
        return ResponseEntity.ok(response.size());
    }

    @GetMapping("/total-processed-amount")
    public ResponseEntity<?> getTotalProcessedAmount(){
        Double response = refundService.getTotalProcessedAmount();
        return ResponseEntity.ok(response);
    }

}
