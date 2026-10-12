package com.luxora.hotel.dto;

import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.RoomStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RoomDto {

    public record CreateRequest(
            @NotBlank(message = "Room number is required") String roomNumber,
            @NotBlank(message = "Room type is required") String roomType,
            @Positive(message = "Price per night must be greater than zero") double pricePerNight,
            @Positive(message = "Max guests must be greater than zero") int maxGuests,
            @NotNull(message = "Room status is required") RoomStatus status,
            String description
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "Room number is required") String roomNumber,
            @NotBlank(message = "Room type is required") String roomType,
            @Positive(message = "Price per night must be greater than zero") double pricePerNight,
            @Positive(message = "Max guests must be greater than zero") int maxGuests,
            @NotNull(message = "Room status is required") RoomStatus status,
            String description
    ) {
    }

    public record Response(
            Long id,
            String roomNumber,
            String roomType,
            double pricePerNight,
            int maxGuests,
            RoomStatus status,
            String description
    ) {
        public static Response fromEntity(Room room) {
            if (room == null) {
                return null;
            }

            return new Response(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getPricePerNight(),
                    room.getMaxGuests(),
                    room.getStatus(),
                    room.getDescription()
            );
        }
    }
}
