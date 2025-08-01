package com.houserental.repository;

import com.houserental.entity.House;
import com.houserental.entity.Review;
import com.houserental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByHouseOrderByCreatedAtDesc(House house);
    
    List<Review> findByReviewer(User reviewer);
    
    Optional<Review> findByHouseAndReviewer(House house, User reviewer);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.house = :house")
    Double findAverageRatingByHouse(@Param("house") House house);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.house = :house")
    long countByHouse(@Param("house") House house);
    
    boolean existsByHouseAndReviewer(House house, User reviewer);
}

