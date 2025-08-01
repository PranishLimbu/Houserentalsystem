package com.houserental.controller;

import com.houserental.entity.House;
import com.houserental.entity.User;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/my-houses")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String myHouses(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            List<House> houses = houseService.findByOwner(user.get());
            model.addAttribute("houses", houses);
        }
        
        return "my-houses";
    }

    @GetMapping("/add")
    public String addHouseForm(Model model) {
        model.addAttribute("house", new House());
        model.addAttribute("propertyTypes", House.PropertyType.values());
        return "add-house";
    }

    @PostMapping("/add")
    public String addHouse(@Valid @ModelAttribute("house") House house, 
                          BindingResult result, 
                          Model model, 
                          RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("propertyTypes", House.PropertyType.values());
            return "add-house";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            house.setOwner(user.get());
            houseService.saveHouse(house);
            redirectAttributes.addFlashAttribute("successMessage", "House added successfully!");
            return "redirect:/my-houses";
        }
        
        model.addAttribute("errorMessage", "Error adding house. Please try again.");
        model.addAttribute("propertyTypes", House.PropertyType.values());
        return "add-house";
    }

    @GetMapping("/edit/{id}")
    public String editHouseForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent()) {
            Optional<House> house = houseService.findById(id);
            if (house.isPresent() && house.get().getOwner().getId().equals(user.get().getId())) {
                model.addAttribute("house", house.get());
                model.addAttribute("propertyTypes", House.PropertyType.values());
                model.addAttribute("availabilityStatuses", House.AvailabilityStatus.values());
                return "edit-house";
            }
        }
        
        return "redirect:/my-houses";
    }

    @PostMapping("/edit/{id}")
    public String editHouse(@PathVariable Long id, 
                           @Valid @ModelAttribute("house") House house, 
                           BindingResult result, 
                           Model model, 
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("propertyTypes", House.PropertyType.values());
            model.addAttribute("availabilityStatuses", House.AvailabilityStatus.values());
            return "edit-house";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent() && houseService.isOwner(id, user.get())) {
            house.setId(id);
            house.setOwner(user.get());
            houseService.updateHouse(house);
            redirectAttributes.addFlashAttribute("successMessage", "House updated successfully!");
            return "redirect:/my-houses";
        }
        
        model.addAttribute("errorMessage", "Error updating house. Please try again.");
        model.addAttribute("propertyTypes", House.PropertyType.values());
        model.addAttribute("availabilityStatuses", House.AvailabilityStatus.values());
        return "edit-house";
    }

    @PostMapping("/delete/{id}")
    public String deleteHouse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(auth.getName());
        
        if (user.isPresent() && houseService.isOwner(id, user.get())) {
            houseService.deleteHouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "House deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting house.");
        }
        
        return "redirect:/my-houses";
    }
}

