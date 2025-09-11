package com.hotel.service;

import com.hotel.dto.RoomFilterRequest;
import com.hotel.entity.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsAvailableTrue();
    }

    public List<Room> getAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRooms(checkInDate, checkOutDate);
    }

    public List<Room> getRoomsByType(String roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    public List<Room> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Room> getRoomsWithFilters(RoomFilterRequest filterRequest) {
        return roomRepository.findRoomsWithFilters(
                filterRequest.getRoomType(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getMinRating(),
                filterRequest.getAmenities()
        );
    }

    public List<String> getDistinctRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    public List<String> getDistinctAmenities() {
        return roomRepository.findDistinctAmenities();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Room room) {
        return roomRepository.save(room);
    }

    public boolean deleteRoom(Long id) {
        // Check if room exists
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            throw new RuntimeException("Room not found");
        }
        
        // Check if room has active reservations
        boolean hasActiveReservations = reservationRepository.hasActiveReservations(id);
        if (hasActiveReservations) {
            return false; // Cannot delete room with active reservations
        }
        
        // Delete the room
        roomRepository.deleteById(id);
        return true; // Successfully deleted
    }
}
