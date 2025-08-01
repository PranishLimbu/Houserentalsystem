package com.houserental.repository;

import com.houserental.entity.Booking;
import com.houserental.entity.House;
import com.houserental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByTenant(User tenant);
    
    List<Booking> findByHouse(House house);
    
    List<Booking> findByTenantOrderByCreatedAtDesc(User tenant);
    
    @Query("SELECT b FROM Booking b WHERE b.house.owner = :owner ORDER BY b.createdAt DESC")
    List<Booking> findByHouseOwner(@Param("owner") User owner);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.house = :house AND b.status IN ('APPROVED', 'ACTIVE') AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findConflictingBookings(@Param("house") House house, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tenant = :tenant")
    long countByTenant(@Param("tenant") User tenant);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.house.owner = :owner AND b.status = 'PENDING'")
    long countPendingBookingsByOwner(@Param("owner") User owner);
}

