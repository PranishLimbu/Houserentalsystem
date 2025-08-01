package com.houserental.service;

import com.houserental.entity.House;
import com.houserental.entity.Review;
import com.houserental.entity.User;
import com.houserental.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        // Check if user has already reviewed this house
        if (reviewRepository.existsByHouseAndReviewer(review.getHouse(), review.getReviewer())) {
            throw new RuntimeException("You have already reviewed this property");
        }
        return reviewRepository.save(review);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByHouse(House house) {
        return reviewRepository.findByHouseOrderByCreatedAtDesc(house);
    }

    public List<Review> findByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }

    public Double getAverageRating(House house) {
        return reviewRepository.findAverageRatingByHouse(house);
    }

    public long countByHouse(House house) {
        return reviewRepository.countByHouse(house);
    }

    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public boolean hasUserReviewed(House house, User user) {
        return reviewRepository.existsByHouseAndReviewer(house, user);
    }

    public boolean isReviewOwner(Long reviewId, User user) {
        Optional<Review> review = findById(reviewId);
        return review.isPresent() && review.get().getReviewer().getId().equals(user.getId());
    }
}

