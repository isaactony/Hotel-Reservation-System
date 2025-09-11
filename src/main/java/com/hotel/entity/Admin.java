package com.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    
    @Column(name = "admin_level")
    private String adminLevel = "ADMIN";

    @Column(name = "department")
    private String department;

    public Admin() {
        super();
    }

    public Admin(String username, String password, String email, String firstName, String lastName, String phoneNumber, String department) {
        super(username, password, email, firstName, lastName, phoneNumber);
        this.department = department;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
