package com.hotel.controller;

import com.hotel.dto.RoomFilterRequest;
import com.hotel.dto.WishlistResponse;
import com.hotel.entity.Room;
import com.hotel.service.RoomService;
import com.hotel.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/available/dates")
    public ResponseEntity<List<Room>> getAvailableRoomsForDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        try {
            List<Room> rooms = roomService.getAvailableRoomsForDates(checkInDate, checkOutDate);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getRoomTypes() {
        List<String> types = roomService.getDistinctRoomTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/amenities")
    public ResponseEntity<List<String>> getAmenities() {
        List<String> amenities = roomService.getDistinctAmenities();
        return ResponseEntity.ok(amenities);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Room>> filterRooms(@RequestBody RoomFilterRequest filterRequest) {
        try {
            List<Room> rooms = roomService.getRoomsWithFilters(filterRequest);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        try {
            Room createdRoom = roomService.createRoom(room);
            return ResponseEntity.ok(createdRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(room -> ResponseEntity.ok(room))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/wishlist/{visitorId}")
    public ResponseEntity<List<WishlistResponse>> getVisitorWishlist(@PathVariable Long visitorId) {
        try {
            List<WishlistResponse> wishlist = wishlistService.getVisitorWishlist(visitorId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/wishlist/{visitorId}/{roomId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long visitorId, 
                                                         @PathVariable Long roomId,
                                                         @RequestParam(required = false) String notes) {
        try {
            WishlistResponse wishlist = wishlistService.addToWishlist(visitorId, roomId, notes);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/wishlist/{visitorId}/{roomId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable Long visitorId, 
                                                                 @PathVariable Long roomId) {
        try {
            wishlistService.removeFromWishlist(visitorId, roomId);
            Map<String, String> response = Map.of("message", "Room removed from wishlist");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/wishlist/{visitorId}/{roomId}/check")
    public ResponseEntity<Map<String, Boolean>> isInWishlist(@PathVariable Long visitorId, 
                                                            @PathVariable Long roomId) {
        boolean inWishlist = wishlistService.isInWishlist(visitorId, roomId);
        Map<String, Boolean> response = Map.of("inWishlist", inWishlist);
        return ResponseEntity.ok(response);
    }
}