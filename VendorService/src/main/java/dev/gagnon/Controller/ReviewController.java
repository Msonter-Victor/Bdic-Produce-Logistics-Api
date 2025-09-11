package dev.gagnon.Controller;

import dev.gagnon.DTO.request.ReviewRequest;
import dev.gagnon.DTO.response.ReviewResponse;
import dev.gagnon.Exception.VendorBaseException;
import dev.gagnon.Model.Review;
import dev.gagnon.Service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review-product")
    public ResponseEntity<?> reviewProduct(@RequestBody ReviewRequest request) {
        try{
            String response = reviewService.reviewProduct(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all-product")
    public ResponseEntity<?> getAllProductReviews(@RequestParam Long productId){
        try{
            List<ReviewResponse> response = reviewService.getProductReviews(productId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-shop")
    public ResponseEntity<?> getShopReviews(@RequestParam Long shopId){
        try{
            List<ReviewResponse> response = reviewService.getShopReviews(shopId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (VendorBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



}
