package com.luxora.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.RoomStatus;

public interface RoomService {
    Room saveRoom(Room room);
    Optional<Room> getRoomById(Long roomId);
    Optional<Room> getRoomByNumber(String roomNumber);
    List<Room> getAllRooms();
    List<Room> getAvailableRooms();
    List<Room> getAvailableRoomsForDates(LocalDate checkInDate, LocalDate checkOutDate, String roomType, Integer guests);
    List<Room> getRoomsByType(String roomType);
    boolean isRoomAvailableForDates(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
    Room updateRoomStatus(Long roomId, RoomStatus status);
}
