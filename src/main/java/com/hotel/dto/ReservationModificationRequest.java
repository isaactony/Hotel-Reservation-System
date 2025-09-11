package com.hotel.dto;

import java.time.LocalDate;

public class ReservationModificationRequest {
    private Long reservationId;
    private LocalDate newCheckInDate;
    private LocalDate newCheckOutDate;
    private Integer newNumberOfGuests;
    private String newSpecialRequests;

    // Constructors
    public ReservationModificationRequest() {}

    public ReservationModificationRequest(Long reservationId, LocalDate newCheckInDate, 
                                        LocalDate newCheckOutDate, Integer newNumberOfGuests, 
                                        String newSpecialRequests) {
        this.reservationId = reservationId;
        this.newCheckInDate = newCheckInDate;
        this.newCheckOutDate = newCheckOutDate;
        this.newNumberOfGuests = newNumberOfGuests;
        this.newSpecialRequests = newSpecialRequests;
    }

    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getNewCheckInDate() {
        return newCheckInDate;
    }

    public void setNewCheckInDate(LocalDate newCheckInDate) {
        this.newCheckInDate = newCheckInDate;
    }

    public LocalDate getNewCheckOutDate() {
        return newCheckOutDate;
    }

    public void setNewCheckOutDate(LocalDate newCheckOutDate) {
        this.newCheckOutDate = newCheckOutDate;
    }

    public Integer getNewNumberOfGuests() {
        return newNumberOfGuests;
    }

    public void setNewNumberOfGuests(Integer newNumberOfGuests) {
        this.newNumberOfGuests = newNumberOfGuests;
    }

    public String getNewSpecialRequests() {
        return newSpecialRequests;
    }

    public void setNewSpecialRequests(String newSpecialRequests) {
        this.newSpecialRequests = newSpecialRequests;
    }
}
