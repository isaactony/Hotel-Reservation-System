package com.hotel.controller;

import com.hotel.dto.ReservationModificationRequest;
import com.hotel.dto.ReservationRequest;
import com.hotel.dto.ReservationResponse;
import com.hotel.entity.Reservation;
import com.hotel.entity.ReservationStatus;
import com.hotel.entity.User;
import com.hotel.entity.Visitor;
import com.hotel.security.CustomUserPrincipal;
import com.hotel.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            // For now, we'll use a simple approach - get visitor ID from request or use a default
            // In a real implementation, this would come from authentication
            Long visitorId = request.getVisitorId() != null ? request.getVisitorId() : 3L; // Default to visitor1
            
            Reservation reservation = reservationService.createReservation(
                visitorId,
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getNumberOfGuests(),
                request.getSpecialRequests()
            );
            
            ReservationResponse response = convertToResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByVisitor(@PathVariable Long visitorId) {
        List<Reservation> reservations = reservationService.getReservationsByVisitor(visitorId);
        List<ReservationResponse> responses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active/visitor/{visitorId}")
    public ResponseEntity<List<ReservationResponse>> getActiveReservationsByVisitor(@PathVariable Long visitorId) {
        List<Reservation> reservations = reservationService.getActiveReservationsByVisitor(visitorId);
        List<ReservationResponse> responses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationResponse> responses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        if (reservation.isPresent()) {
            ReservationResponse response = convertToResponse(reservation.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = Map.of("error", "Reservation not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
            Reservation reservation = reservationService.updateReservationStatus(id, reservationStatus);
            ReservationResponse response = convertToResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            Map<String, String> response = Map.of("message", "Reservation cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ReservationResponse>> getReservationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Reservation> reservations = reservationService.getReservationsByDateRange(startDate, endDate);
        List<ReservationResponse> responses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private ReservationResponse convertToResponse(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getVisitor().getId(),
            reservation.getVisitor().getFirstName() + " " + reservation.getVisitor().getLastName(),
            reservation.getRoom().getId(),
            reservation.getRoom().getRoomNumber(),
            reservation.getRoom().getRoomType(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getNumberOfGuests(),
            reservation.getTotalAmount() != null ? reservation.getTotalAmount().doubleValue() : 0.0,
            reservation.getStatus().toString(),
            reservation.getSpecialRequests(),
            reservation.getCreatedAt(),
            reservation.getUpdatedAt()
        );
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyReservation(@Valid @RequestBody ReservationModificationRequest request) {
        try {
            Reservation reservation = reservationService.modifyReservation(request);
            ReservationResponse response = convertToResponse(reservation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
