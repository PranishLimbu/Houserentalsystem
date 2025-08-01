package com.houserental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "houses")
public class House {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Column(nullable = false)
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    @Column(nullable = false)
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters")
    @Column(nullable = false)
    private String state;
    
    @NotBlank(message = "ZIP code is required")
    @Size(min = 5, max = 20, message = "ZIP code must be between 5 and 20 characters")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(nullable = false)
    private String country = "USA";
    
    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price_per_month", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMonth;
    
    @NotNull(message = "Security deposit is required")
    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    @Column(name = "security_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal securityDeposit;
    
    @Min(value = 0, message = "Bedrooms must be non-negative")
    @Max(value = 20, message = "Bedrooms must not exceed 20")
    private Integer bedrooms;
    
    @Min(value = 0, message = "Bathrooms must be non-negative")
    @Max(value = 20, message = "Bathrooms must not exceed 20")
    private Integer bathrooms;
    
    @Min(value = 1, message = "Square feet must be positive")
    @Column(name = "square_feet")
    private Integer squareFeet;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;
    
    @Column(name = "pets_allowed")
    private Boolean petsAllowed = false;
    
    @Column(name = "smoking_allowed")
    private Boolean smokingAllowed = false;
    
    private Boolean furnished = false;
    
    @Column(name = "parking_available")
    private Boolean parkingAvailable = false;
    
    @Column(name = "laundry_available")
    private Boolean laundryAvailable = false;
    
    @Column(name = "air_conditioning")
    private Boolean airConditioning = false;
    
    private Boolean heating = false;
    
    private Boolean internet = false;
    
    @ElementCollection
    @CollectionTable(name = "house_images", joinColumns = @JoinColumn(name = "house_id"))
    @Column(name = "image_url")
    private List<String> images;
    
    @ElementCollection
    @CollectionTable(name = "house_amenities", joinColumns = @JoinColumn(name = "house_id"))
    @Column(name = "amenity")
    private List<String> amenities;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
    
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PropertyType {
        APARTMENT, HOUSE, CONDO, TOWNHOUSE, STUDIO, ROOM
    }
    
    public enum AvailabilityStatus {
        AVAILABLE, RENTED, MAINTENANCE, UNAVAILABLE
    }
    
    // Constructors
    public House() {}
    
    public House(String title, String description, String address, String city, String state, 
                 String zipCode, BigDecimal pricePerMonth, BigDecimal securityDeposit, 
                 PropertyType propertyType, User owner) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.pricePerMonth = pricePerMonth;
        this.securityDeposit = securityDeposit;
        this.propertyType = propertyType;
        this.owner = owner;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(BigDecimal pricePerMonth) { this.pricePerMonth = pricePerMonth; }
    
    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }
    
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    
    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    
    public Integer getSquareFeet() { return squareFeet; }
    public void setSquareFeet(Integer squareFeet) { this.squareFeet = squareFeet; }
    
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public Boolean getPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(Boolean petsAllowed) { this.petsAllowed = petsAllowed; }
    
    public Boolean getSmokingAllowed() { return smokingAllowed; }
    public void setSmokingAllowed(Boolean smokingAllowed) { this.smokingAllowed = smokingAllowed; }
    
    public Boolean getFurnished() { return furnished; }
    public void setFurnished(Boolean furnished) { this.furnished = furnished; }
    
    public Boolean getParkingAvailable() { return parkingAvailable; }
    public void setParkingAvailable(Boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }
    
    public Boolean getLaundryAvailable() { return laundryAvailable; }
    public void setLaundryAvailable(Boolean laundryAvailable) { this.laundryAvailable = laundryAvailable; }
    
    public Boolean getAirConditioning() { return airConditioning; }
    public void setAirConditioning(Boolean airConditioning) { this.airConditioning = airConditioning; }
    
    public Boolean getHeating() { return heating; }
    public void setHeating(Boolean heating) { this.heating = heating; }
    
    public Boolean getInternet() { return internet; }
    public void setInternet(Boolean internet) { this.internet = internet; }
    
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFullAddress() {
        return address + ", " + city + ", " + state + " " + zipCode;
    }
}

