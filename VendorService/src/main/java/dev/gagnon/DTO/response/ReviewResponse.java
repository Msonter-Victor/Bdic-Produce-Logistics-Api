package dev.gagnon.DTO.response;

import dev.gagnon.Model.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private int rating;
    private String comment;
    private String productUrl;
    private String productName;
    private String reviewerProfileUrl;
    private String reviewerFirstName;
    private String reviewerLastName;
    private LocalDateTime reviewedAt;
    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewedAt= review.getReviewDate();
        this.reviewerProfileUrl=review.getReviewer().getMediaUrl();
        this.reviewerFirstName=review.getReviewer().getFirstName();
        this.reviewerLastName=review.getReviewer().getLastName();
        this.productName = review.getProduct().getName();
        this.productUrl = review.getProduct().getMainImageUrl();
    }

}
