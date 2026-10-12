package com.luxora.hotel.model;

public class StandardRoom extends Room {
    public StandardRoom(String roomNumber, double pricePerNight, int maxGuests) {
        super(roomNumber, pricePerNight, maxGuests, RoomStatus.AVAILABLE,
                "Comfortable room with queen bed, Wi-Fi and a cozy lounge area.");
    }

    @Override
    public String getRoomType() {
        return "Standard Room";
    }

    @Override
    public String getFeatures() {
        return "Queen bed, work desk, smart TV, Wi-Fi, marble bath";
    }
}
