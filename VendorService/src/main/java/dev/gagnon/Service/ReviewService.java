package dev.gagnon.Service;

import dev.gagnon.DTO.request.ReviewRequest;
import dev.gagnon.DTO.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    String reviewProduct(ReviewRequest request);

    List<ReviewResponse> getProductReviews(Long productId);

    List<ReviewResponse> getShopReviews(Long shopId);
}
