package com.hotel.repository;

import com.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByRoomType(String roomType);
    List<Room> findByIsAvailableTrue();
    List<Room> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE " +
           "res.status IN ('CONFIRMED', 'CHECKED_IN') AND " +
           "((res.checkInDate <= :checkOutDate AND res.checkOutDate >= :checkInDate)))")
    List<Room> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate, 
                                  @Param("checkOutDate") LocalDate checkOutDate);
    
    // Advanced filtering methods
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR r.rating >= :minRating) AND " +
           "(:amenities IS NULL OR r.amenities LIKE %:amenities%)")
    List<Room> findRoomsWithFilters(@Param("roomType") String roomType,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minRating") Double minRating,
                                   @Param("amenities") String amenities);
    
    @Query("SELECT DISTINCT r.roomType FROM Room r WHERE r.isAvailable = true")
    List<String> findDistinctRoomTypes();
    
    @Query("SELECT DISTINCT r.amenities FROM Room r WHERE r.isAvailable = true AND r.amenities IS NOT NULL")
    List<String> findDistinctAmenities();
}
