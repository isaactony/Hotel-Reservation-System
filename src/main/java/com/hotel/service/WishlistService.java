package com.hotel.service;

import com.hotel.dto.WishlistResponse;
import com.hotel.entity.Room;
import com.hotel.entity.Visitor;
import com.hotel.entity.Wishlist;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.VisitorRepository;
import com.hotel.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    public List<WishlistResponse> getVisitorWishlist(Long visitorId) {
        List<Wishlist> wishlists = wishlistRepository.findByVisitorIdOrderByCreatedAtDesc(visitorId);
        return wishlists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public WishlistResponse addToWishlist(Long visitorId, Long roomId, String notes) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if already in wishlist
        if (wishlistRepository.existsByVisitorIdAndRoomId(visitorId, roomId)) {
            throw new RuntimeException("Room already in wishlist");
        }

        Wishlist wishlist = new Wishlist(visitor, room, notes);
        wishlist = wishlistRepository.save(wishlist);
        
        return convertToResponse(wishlist);
    }

    public void removeFromWishlist(Long visitorId, Long roomId) {
        if (!wishlistRepository.existsByVisitorIdAndRoomId(visitorId, roomId)) {
            throw new RuntimeException("Room not found in wishlist");
        }
        wishlistRepository.deleteByVisitorIdAndRoomId(visitorId, roomId);
    }

    public boolean isInWishlist(Long visitorId, Long roomId) {
        return wishlistRepository.existsByVisitorIdAndRoomId(visitorId, roomId);
    }

    private WishlistResponse convertToResponse(Wishlist wishlist) {
        Room room = wishlist.getRoom();
        return new WishlistResponse(
                wishlist.getId(),
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getDescription(),
                room.getPhotoUrl(),
                room.getRating(),
                wishlist.getCreatedAt(),
                wishlist.getNotes()
        );
    }
}
