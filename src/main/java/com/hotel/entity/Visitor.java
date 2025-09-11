package com.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "visitors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Visitor extends User {
    
    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "preferences", length = 1000)
    private String preferences;

    public Visitor() {
        super();
    }

    public Visitor(String username, String password, String email, String firstName, String lastName, String phoneNumber, String address, String city, String country, String postalCode) {
        super(username, password, email, firstName, lastName, phoneNumber);
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
