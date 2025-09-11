package com.hotel.service;

import com.hotel.entity.Admin;
import com.hotel.entity.User;
import com.hotel.entity.Visitor;
import com.hotel.repository.AdminRepository;
import com.hotel.repository.UserRepository;
import com.hotel.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin registerAdmin(Admin admin) {
        if (userRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Visitor registerVisitor(Visitor visitor) {
        if (userRepository.existsByUsername(visitor.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(visitor.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        visitor.setPassword(passwordEncoder.encode(visitor.getPassword()));
        return visitorRepository.save(visitor);
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        return user;
    }
}
