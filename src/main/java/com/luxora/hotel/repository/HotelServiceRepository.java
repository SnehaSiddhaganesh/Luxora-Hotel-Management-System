package com.luxora.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.HotelService;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {
    Optional<HotelService> findByName(String name);
    List<HotelService> findByNameContainingIgnoreCase(String name);
}