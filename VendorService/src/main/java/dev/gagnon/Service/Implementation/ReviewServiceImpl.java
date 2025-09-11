package dev.gagnon.Service.Implementation;

import dev.gagnon.DTO.request.ReviewRequest;
import dev.gagnon.DTO.response.ReviewResponse;
import dev.gagnon.Exception.BusinessException;
import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.Product;
import dev.gagnon.Model.Review;
import dev.gagnon.Model.User;
import dev.gagnon.Repository.ProductRepository;
import dev.gagnon.Repository.ReviewRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public String reviewProduct(ReviewRequest request) {
        Review existingReview = reviewRepository.findByReviewerAndProduct(request.getEmail(),request.getProductId());
        if (existingReview != null) throw new BusinessException("Already reviewed this product");
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review review = new Review();
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setProduct(product);
        review.setReviewer(user);
        reviewRepository.save(review);
        return "Review added for product";
    }

    @Override
    public List<ReviewResponse> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty())return List.of();
        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();

    }

    @Override
    public List<ReviewResponse> getShopReviews(Long shopId) {
        List<Review> reviews = reviewRepository.findByShopId(shopId);
        if (reviews.isEmpty())return List.of();
        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    

}
