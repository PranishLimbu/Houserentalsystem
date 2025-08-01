package com.houserental.repository;

import com.houserental.entity.House;
import com.houserental.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    
    Page<House> findByAvailabilityStatus(House.AvailabilityStatus status, Pageable pageable);
    
    List<House> findByOwner(User owner);
    
    Page<House> findByOwner(User owner, Pageable pageable);
    
    @Query("SELECT h FROM House h WHERE " +
           "(:keyword IS NULL OR LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(h.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:minPrice IS NULL OR h.pricePerMonth >= :minPrice) AND " +
           "(:maxPrice IS NULL OR h.pricePerMonth <= :maxPrice) AND " +
           "(:bedrooms IS NULL OR h.bedrooms >= :bedrooms) AND " +
           "(:bathrooms IS NULL OR h.bathrooms >= :bathrooms) AND " +
           "(:propertyType IS NULL OR h.propertyType = :propertyType) AND " +
           "h.availabilityStatus = 'AVAILABLE'")
    Page<House> searchHouses(@Param("keyword") String keyword,
                            @Param("city") String city,
                            @Param("state") String state,
                            @Param("minPrice") BigDecimal minPrice,
                            @Param("maxPrice") BigDecimal maxPrice,
                            @Param("bedrooms") Integer bedrooms,
                            @Param("bathrooms") Integer bathrooms,
                            @Param("propertyType") House.PropertyType propertyType,
                            Pageable pageable);
    
    @Query("SELECT h FROM House h WHERE h.availabilityStatus = 'AVAILABLE' ORDER BY h.createdAt DESC")
    Page<House> findAvailableHouses(Pageable pageable);
    
    @Query("SELECT COUNT(h) FROM House h WHERE h.owner = :owner")
    long countByOwner(@Param("owner") User owner);
}

