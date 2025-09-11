package com.hotel.repository;

import com.hotel.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByCity(String city);
    List<Visitor> findByCountry(String country);
}
