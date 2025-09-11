package com.hotel.repository;

import com.hotel.entity.Reservation;
import com.hotel.entity.ReservationStatus;
import com.hotel.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVisitor(Visitor visitor);
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsByDateRange(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.visitor.id = :visitorId AND r.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING')")
    List<Reservation> findActiveReservationsByVisitor(@Param("visitorId") Long visitorId);
    
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.room.id = :roomId AND r.status IN ('CONFIRMED', 'CHECKED_IN', 'PENDING')")
    boolean hasActiveReservations(@Param("roomId") Long roomId);
}
