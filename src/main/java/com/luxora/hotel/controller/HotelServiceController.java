package com.luxora.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.HotelServiceDto;
import com.luxora.hotel.entity.HotelService;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.repository.HotelServiceRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotel-services")
public class HotelServiceController {

    private final HotelServiceRepository hotelServiceRepository;

    public HotelServiceController(HotelServiceRepository hotelServiceRepository) {
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @GetMapping
    public List<HotelServiceDto.Response> getAllHotelServices(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return hotelServiceRepository.findByNameContainingIgnoreCase(name)
                    .stream()
                    .map(HotelServiceDto.Response::fromEntity)
                    .toList();
        }

        return hotelServiceRepository.findAll().stream().map(HotelServiceDto.Response::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public HotelServiceDto.Response getHotelServiceById(@PathVariable Long id) {
        HotelService hotelService = hotelServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel service not found with id: " + id));
        return HotelServiceDto.Response.fromEntity(hotelService);
    }

    @PostMapping
    public ResponseEntity<HotelServiceDto.Response> createHotelService(@Valid @RequestBody HotelServiceDto.CreateRequest request) {
        HotelService hotelService = new HotelService();
        hotelService.setName(request.name());
        hotelService.setDescription(request.description());
        hotelService.setBasePrice(request.basePrice());

        HotelService saved = hotelServiceRepository.save(hotelService);
        return ResponseEntity.status(HttpStatus.CREATED).body(HotelServiceDto.Response.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelServiceDto.Response> updateHotelService(
            @PathVariable Long id,
            @Valid @RequestBody HotelServiceDto.UpdateRequest request
    ) {
        HotelService hotelService = hotelServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel service not found with id: " + id));

        hotelService.setName(request.name());
        hotelService.setDescription(request.description());
        hotelService.setBasePrice(request.basePrice());

        HotelService updated = hotelServiceRepository.save(hotelService);
        return ResponseEntity.ok(HotelServiceDto.Response.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelService(@PathVariable Long id) {
        if (!hotelServiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel service not found with id: " + id);
        }

        hotelServiceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
