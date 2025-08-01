package com.houserental.service;

import com.houserental.entity.Booking;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking saveBooking(Booking booking) {
        // Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                booking.getHouse(), booking.getStartDate(), booking.getEndDate());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Property is not available for the selected dates");
        }
        
        return bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> findByTenant(User tenant) {
        return bookingRepository.findByTenantOrderByCreatedAtDesc(tenant);
    }

    public List<Booking> findByHouseOwner(User owner) {
        return bookingRepository.findByHouseOwner(owner);
    }

    public List<Booking> findByHouse(House house) {
        return bookingRepository.findByHouse(house);
    }

    public Booking updateBookingStatus(Long bookingId, Booking.BookingStatus status, String rejectionReason) {
        Optional<Booking> optionalBooking = findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(status);
            if (status == Booking.BookingStatus.REJECTED && rejectionReason != null) {
                booking.setRejectionReason(rejectionReason);
            }
            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found");
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public long countByTenant(User tenant) {
        return bookingRepository.countByTenant(tenant);
    }

    public long countPendingBookingsByOwner(User owner) {
        return bookingRepository.countPendingBookingsByOwner(owner);
    }

    public boolean isBookingOwner(Long bookingId, User user) {
        Optional<Booking> booking = findById(bookingId);
        return booking.isPresent() && 
               (booking.get().getTenant().getId().equals(user.getId()) || 
                booking.get().getHouse().getOwner().getId().equals(user.getId()));
    }

    public boolean canUserManageBooking(Long bookingId, User user) {
        Optional<Booking> booking = findById(bookingId);
        return booking.isPresent() && 
               booking.get().getHouse().getOwner().getId().equals(user.getId());
    }
}

