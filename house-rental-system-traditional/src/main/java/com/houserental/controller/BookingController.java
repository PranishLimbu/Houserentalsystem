package com.houserental.controller;

import com.houserental.entity.Booking;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.service.BookingService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            User currentUser = user.get();
            List<Booking> bookings;
            
            if (currentUser.getRole() == User.Role.LANDLORD) {
                bookings = bookingService.findByHouseOwner(currentUser);
                model.addAttribute("isLandlord", true);
            } else {
                bookings = bookingService.findByTenant(currentUser);
                model.addAttribute("isLandlord", false);
            }
            
            model.addAttribute("bookings", bookings);
            model.addAttribute("bookingStatuses", Booking.BookingStatus.values());
        }
        
        return "my-bookings";
    }

    @GetMapping("/book/{houseId}")
    public String bookHouseForm(@PathVariable Long houseId, Model model) {
        Optional<House> house = houseService.findById(houseId);
        if (house.isPresent()) {
            model.addAttribute("house", house.get());
            model.addAttribute("booking", new Booking());
            return "book-house";
        }
        return "redirect:/houses";
    }

    @PostMapping("/book/{houseId}")
    public String bookHouse(@PathVariable Long houseId, 
                           @Valid @ModelAttribute("booking") Booking booking, 
                           BindingResult result, 
                           Model model, 
                           RedirectAttributes redirectAttributes) {
        
        Optional<House> house = houseService.findById(houseId);
        if (!house.isPresent()) {
            return "redirect:/houses";
        }

        if (result.hasErrors()) {
            model.addAttribute("house", house.get());
            return "book-house";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            try {
                // Calculate total amount
                long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
                BigDecimal totalAmount = house.get().getPricePerMonth()
                    .multiply(BigDecimal.valueOf(days))
                    .divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_HALF_UP);
                
                booking.setHouse(house.get());
                booking.setTenant(user.get());
                booking.setTotalAmount(totalAmount);
                
                bookingService.saveBooking(booking);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Booking request submitted successfully! The landlord will review your request.");
                return "redirect:/my-bookings";
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("house", house.get());
                return "book-house";
            }
        }
        
        return "redirect:/login";
    }

    @PostMapping("/bookings/{id}/approve")
    public String approveBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent() && bookingService.canUserManageBooking(id, user.get())) {
            try {
                bookingService.updateBookingStatus(id, Booking.BookingStatus.APPROVED, null);
                redirectAttributes.addFlashAttribute("successMessage", "Booking approved successfully!");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to manage this booking.");
        }
        
        return "redirect:/my-bookings";
    }

    @PostMapping("/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id, 
                               @RequestParam String rejectionReason, 
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent() && bookingService.canUserManageBooking(id, user.get())) {
            try {
                bookingService.updateBookingStatus(id, Booking.BookingStatus.REJECTED, rejectionReason);
                redirectAttributes.addFlashAttribute("successMessage", "Booking rejected successfully!");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to manage this booking.");
        }
        
        return "redirect:/my-bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent() && bookingService.isBookingOwner(id, user.get())) {
            try {
                bookingService.updateBookingStatus(id, Booking.BookingStatus.CANCELLED, null);
                redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully!");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have permission to cancel this booking.");
        }
        
        return "redirect:/my-bookings";
    }
}

