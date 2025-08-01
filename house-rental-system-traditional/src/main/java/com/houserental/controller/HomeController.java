package com.houserental.controller;

import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.service.BookingService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<House> houses = houseService.findAllAvailable(pageable);
        
        model.addAttribute("houses", houses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", houses.getTotalPages());
        
        return "index";
    }

    @GetMapping("/houses")
    public String houses(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 12);
        Page<House> houses = houseService.findAllAvailable(pageable);
        
        model.addAttribute("houses", houses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", houses.getTotalPages());
        
        return "houses";
    }

    @GetMapping("/houses/{id}")
    public String houseDetails(@PathVariable Long id, Model model) {
        Optional<House> house = houseService.findById(id);
        if (house.isPresent()) {
            model.addAttribute("house", house.get());
            return "house-details";
        }
        return "redirect:/houses";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String city,
                        @RequestParam(required = false) String state,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(required = false) Integer bedrooms,
                        @RequestParam(required = false) Integer bathrooms,
                        @RequestParam(required = false) House.PropertyType propertyType,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        
        Pageable pageable = PageRequest.of(page, 12);
        Page<House> houses = houseService.searchHouses(keyword, city, state, minPrice, maxPrice, 
                                                      bedrooms, bathrooms, propertyType, pageable);
        
        model.addAttribute("houses", houses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", houses.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("city", city);
        model.addAttribute("state", state);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("bedrooms", bedrooms);
        model.addAttribute("bathrooms", bathrooms);
        model.addAttribute("propertyType", propertyType);
        model.addAttribute("propertyTypes", House.PropertyType.values());
        
        return "search";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            User currentUser = user.get();
            model.addAttribute("user", currentUser);
            
            if (currentUser.getRole() == User.Role.LANDLORD) {
                long houseCount = houseService.countByOwner(currentUser);
                long pendingBookings = bookingService.countPendingBookingsByOwner(currentUser);
                
                model.addAttribute("houseCount", houseCount);
                model.addAttribute("pendingBookings", pendingBookings);
            } else {
                long bookingCount = bookingService.countByTenant(currentUser);
                model.addAttribute("bookingCount", bookingCount);
            }
        }
        
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "register";
    }
}

