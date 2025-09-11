package com.hotel.dto;

import java.time.LocalDateTime;

public class WishlistResponse {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private String description;
    private String photoUrl;
    private Double rating;
    private LocalDateTime createdAt;
    private String notes;

    // Constructors
    public WishlistResponse() {}

    public WishlistResponse(Long id, Long roomId, String roomNumber, String roomType, 
                           String description, String photoUrl, Double rating, 
                           LocalDateTime createdAt, String notes) {
        this.id = id;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.description = description;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.createdAt = createdAt;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
