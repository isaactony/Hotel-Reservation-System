package com.hotel.service;

import com.hotel.dto.ReservationModificationRequest;
import com.hotel.entity.Reservation;
import com.hotel.entity.ReservationStatus;
import com.hotel.entity.Room;
import com.hotel.entity.Visitor;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private InvoiceService invoiceService;

    public Reservation createReservation(Long visitorId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, String specialRequests) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if room is available for the given dates
        List<Room> availableRooms = roomRepository.findAvailableRooms(checkInDate, checkOutDate);
        if (!availableRooms.contains(room)) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        // Calculate total amount
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal totalAmount = room.getPrice().multiply(BigDecimal.valueOf(days));

        Reservation reservation = new Reservation(visitor, room, checkInDate, checkOutDate, numberOfGuests, specialRequests);
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Automatically create invoice for the reservation
        try {
            invoiceService.createInvoiceForReservation(savedReservation.getId());
        } catch (Exception e) {
            // Log error but don't fail the reservation creation
            System.err.println("Failed to create invoice for reservation " + savedReservation.getId() + ": " + e.getMessage());
        }
        
        return savedReservation;
    }

    public List<Reservation> getReservationsByVisitor(Long visitorId) {
        return reservationRepository.findByVisitor(visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found")));
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findReservationsByDateRange(startDate, endDate);
    }

    public List<Reservation> getActiveReservationsByVisitor(Long visitorId) {
        return reservationRepository.findActiveReservationsByVisitor(visitorId);
    }

    public Reservation modifyReservation(ReservationModificationRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check if reservation can be modified
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cannot modify cancelled reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new RuntimeException("Cannot modify completed reservation");
        }

        // Update dates if provided
        if (request.getNewCheckInDate() != null) {
            reservation.setCheckInDate(request.getNewCheckInDate());
        }
        if (request.getNewCheckOutDate() != null) {
            reservation.setCheckOutDate(request.getNewCheckOutDate());
        }

        // Update number of guests if provided
        if (request.getNewNumberOfGuests() != null) {
            reservation.setNumberOfGuests(request.getNewNumberOfGuests());
        }

        // Update special requests if provided
        if (request.getNewSpecialRequests() != null) {
            reservation.setSpecialRequests(request.getNewSpecialRequests());
        }

        // Recalculate total amount if dates changed
        if (request.getNewCheckInDate() != null || request.getNewCheckOutDate() != null) {
            long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
            BigDecimal totalAmount = reservation.getRoom().getPrice().multiply(BigDecimal.valueOf(days));
            reservation.setTotalAmount(totalAmount);
        }

        return reservationRepository.save(reservation);
    }
}
