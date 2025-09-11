package com.hotel.controller;

import com.hotel.dto.AdminRegistrationRequest;
import com.hotel.dto.LoginRequest;
import com.hotel.dto.VisitorRegistrationRequest;
import com.hotel.entity.Admin;
import com.hotel.entity.User;
import com.hotel.entity.Visitor;
import com.hotel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        try {
            Admin admin = new Admin(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getDepartment()
            );
            
            Admin savedAdmin = authService.registerAdmin(admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin registered successfully");
            response.put("adminId", savedAdmin.getId());
            response.put("username", savedAdmin.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register/visitor")
    public ResponseEntity<?> registerVisitor(@Valid @RequestBody VisitorRegistrationRequest request) {
        try {
            Visitor visitor = new Visitor(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getCity(),
                request.getCountry(),
                request.getPostalCode()
            );
            
            Visitor savedVisitor = authService.registerVisitor(visitor);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Visitor registered successfully");
            response.put("visitorId", savedVisitor.getId());
            response.put("username", savedVisitor.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.authenticate(request.getUsername(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("userType", user instanceof Admin ? "ADMIN" : "VISITOR");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
