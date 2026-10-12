package com.luxora.hotel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.RoomDto;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.exception.ResourceNotFoundException;
import com.luxora.hotel.model.RoomStatus;
import com.luxora.hotel.repository.RoomRepository;
import com.luxora.hotel.service.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    public RoomController(RoomService roomService, RoomRepository roomRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<RoomDto.Response> getAllRooms() {
        return roomService.getAllRooms().stream().map(RoomDto.Response::fromEntity).toList();
    }

    @GetMapping("/available")
    public List<RoomDto.Response> getAvailableRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer guests
    ) {
        return roomService.getAvailableRoomsForDates(checkIn, checkOut, roomType, guests)
                .stream().map(RoomDto.Response::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public RoomDto.Response getRoomById(@PathVariable Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return RoomDto.Response.fromEntity(room);
    }

    @PostMapping
    public ResponseEntity<RoomDto.Response> createRoom(@Valid @RequestBody RoomDto.CreateRequest request) {
        Room room = new Room(
                request.roomNumber(),
                request.roomType(),
                request.pricePerNight(),
                request.maxGuests(),
                request.status(),
                request.description()
        );

        Room savedRoom = roomService.saveRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoomDto.Response.fromEntity(savedRoom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto.Response> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomDto.UpdateRequest request
    ) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        room.setRoomNumber(request.roomNumber());
        room.setRoomType(request.roomType());
        room.setPricePerNight(request.pricePerNight());
        room.setMaxGuests(request.maxGuests());
        room.setStatus(request.status());
        room.setDescription(request.description());

        Room updatedRoom = roomRepository.save(room);
        return ResponseEntity.ok(RoomDto.Response.fromEntity(updatedRoom));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomDto.Response> updateRoomStatus(
            @PathVariable Long id,
            @RequestBody RoomStatus status
    ) {
        Room updatedRoom = roomService.updateRoomStatus(id, status);
        return ResponseEntity.ok(RoomDto.Response.fromEntity(updatedRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }

        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
