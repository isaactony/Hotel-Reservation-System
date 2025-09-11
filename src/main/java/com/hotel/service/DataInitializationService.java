package com.hotel.service;

import com.hotel.entity.Admin;
import com.hotel.entity.Room;
import com.hotel.entity.Visitor;
import com.hotel.repository.AdminRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create sample admins
        if (adminRepository.count() == 0) {
            Admin admin1 = new Admin("admin1", "password123", "admin1@hotel.com", "John", "Admin", "555-0101", "Front Desk");
            admin1.setPassword(passwordEncoder.encode(admin1.getPassword()));
            adminRepository.save(admin1);

            Admin admin2 = new Admin("admin2", "password123", "admin2@hotel.com", "Jane", "Manager", "555-0102", "Management");
            admin2.setPassword(passwordEncoder.encode(admin2.getPassword()));
            adminRepository.save(admin2);
        }

        // Create sample visitors
        if (visitorRepository.count() == 0) {
            Visitor visitor1 = new Visitor("visitor1", "password123", "visitor1@email.com", "Alice", "Smith", "555-0201", "123 Main St", "New York", "USA", "10001");
            visitor1.setPassword(passwordEncoder.encode(visitor1.getPassword()));
            visitorRepository.save(visitor1);

            Visitor visitor2 = new Visitor("visitor2", "password123", "visitor2@email.com", "Bob", "Johnson", "555-0202", "456 Oak Ave", "Los Angeles", "USA", "90210");
            visitor2.setPassword(passwordEncoder.encode(visitor2.getPassword()));
            visitorRepository.save(visitor2);

            Visitor visitor3 = new Visitor("visitor3", "password123", "visitor3@email.com", "Carol", "Williams", "555-0203", "789 Pine St", "Chicago", "USA", "60601");
            visitor3.setPassword(passwordEncoder.encode(visitor3.getPassword()));
            visitorRepository.save(visitor3);
        }

        // Create sample rooms
        if (roomRepository.count() == 0) {
            // Standard Rooms
            Room room101 = new Room("101", "Standard", new BigDecimal("120.00"), 2, "Comfortable standard room with city view", "WiFi, TV, Mini-fridge");
            room101.setPhotoUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500");
            room101.setRating(4.2);
            roomRepository.save(room101);

            Room room102 = new Room("102", "Standard", new BigDecimal("120.00"), 2, "Comfortable standard room with garden view", "WiFi, TV, Mini-fridge");
            room102.setPhotoUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=500");
            room102.setRating(4.0);
            roomRepository.save(room102);

            Room room103 = new Room("103", "Standard", new BigDecimal("120.00"), 2, "Comfortable standard room with city view", "WiFi, TV, Mini-fridge");
            room103.setPhotoUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500");
            room103.setRating(4.1);
            roomRepository.save(room103);

            // Deluxe Rooms
            Room room201 = new Room("201", "Deluxe", new BigDecimal("180.00"), 3, "Spacious deluxe room with premium amenities", "WiFi, Smart TV, Mini-bar, Balcony");
            room201.setPhotoUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=500");
            room201.setRating(4.5);
            roomRepository.save(room201);

            Room room202 = new Room("202", "Deluxe", new BigDecimal("180.00"), 3, "Spacious deluxe room with ocean view", "WiFi, Smart TV, Mini-bar, Balcony");
            room202.setPhotoUrl("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=500");
            room202.setRating(4.6);
            roomRepository.save(room202);

            Room room203 = new Room("203", "Deluxe", new BigDecimal("180.00"), 3, "Spacious deluxe room with mountain view", "WiFi, Smart TV, Mini-bar, Balcony");
            room203.setPhotoUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=500");
            room203.setRating(4.4);
            roomRepository.save(room203);

            // Suite Rooms
            Room room301 = new Room("301", "Suite", new BigDecimal("300.00"), 4, "Luxurious suite with separate living area", "WiFi, Smart TV, Full bar, Jacuzzi, Private balcony");
            room301.setPhotoUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=500");
            room301.setRating(4.8);
            roomRepository.save(room301);

            Room room302 = new Room("302", "Suite", new BigDecimal("300.00"), 4, "Presidential suite with panoramic views", "WiFi, Smart TV, Full bar, Jacuzzi, Private balcony, Butler service");
            room302.setPhotoUrl("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=500");
            room302.setRating(4.9);
            roomRepository.save(room302);

            // Family Rooms
            Room room401 = new Room("401", "Family", new BigDecimal("220.00"), 6, "Large family room with multiple beds", "WiFi, TV, Kitchenette, Play area");
            room401.setPhotoUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500");
            room401.setRating(4.3);
            roomRepository.save(room401);

            Room room402 = new Room("402", "Family", new BigDecimal("220.00"), 6, "Large family room with connecting rooms", "WiFi, TV, Kitchenette, Play area");
            room402.setPhotoUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=500");
            room402.setRating(4.2);
            roomRepository.save(room402);

            // Business Rooms
            Room room501 = new Room("501", "Business", new BigDecimal("200.00"), 2, "Business room with work desk and meeting space", "WiFi, Smart TV, Work desk, Printer, Coffee machine");
            room501.setPhotoUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=500");
            room501.setRating(4.4);
            roomRepository.save(room501);

            Room room502 = new Room("502", "Business", new BigDecimal("200.00"), 2, "Business room with conference facilities", "WiFi, Smart TV, Work desk, Printer, Coffee machine, Meeting table");
            room502.setPhotoUrl("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=500");
            room502.setRating(4.5);
            roomRepository.save(room502);
        }
    }
}