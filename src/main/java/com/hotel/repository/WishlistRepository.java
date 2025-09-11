package com.hotel.repository;

import com.hotel.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    List<Wishlist> findByVisitorId(Long visitorId);
    
    boolean existsByVisitorIdAndRoomId(Long visitorId, Long roomId);
    
    void deleteByVisitorIdAndRoomId(Long visitorId, Long roomId);
    
    @Query("SELECT w FROM Wishlist w WHERE w.visitor.id = :visitorId ORDER BY w.createdAt DESC")
    List<Wishlist> findByVisitorIdOrderByCreatedAtDesc(@Param("visitorId") Long visitorId);
}
