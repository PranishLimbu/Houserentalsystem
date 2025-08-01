package com.houserental.service;

import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;

    public House saveHouse(House house) {
        return houseRepository.save(house);
    }

    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    public Page<House> findAllAvailable(Pageable pageable) {
        return houseRepository.findAvailableHouses(pageable);
    }

    public List<House> findByOwner(User owner) {
        return houseRepository.findByOwner(owner);
    }

    public Page<House> findByOwner(User owner, Pageable pageable) {
        return houseRepository.findByOwner(owner, pageable);
    }

    public Page<House> searchHouses(String keyword, String city, String state, 
                                  BigDecimal minPrice, BigDecimal maxPrice, 
                                  Integer bedrooms, Integer bathrooms, 
                                  House.PropertyType propertyType, Pageable pageable) {
        return houseRepository.searchHouses(keyword, city, state, minPrice, maxPrice, 
                                          bedrooms, bathrooms, propertyType, pageable);
    }

    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }

    public House updateHouse(House house) {
        return houseRepository.save(house);
    }

    public long countByOwner(User owner) {
        return houseRepository.countByOwner(owner);
    }

    public boolean existsById(Long id) {
        return houseRepository.existsById(id);
    }

    public boolean isOwner(Long houseId, User user) {
        Optional<House> house = findById(houseId);
        return house.isPresent() && house.get().getOwner().getId().equals(user.getId());
    }
}

