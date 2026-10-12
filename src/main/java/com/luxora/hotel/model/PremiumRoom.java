package com.luxora.hotel.model;

public class PremiumRoom extends Room {
    public PremiumRoom(String roomNumber, double pricePerNight, int maxGuests) {
        super(roomNumber, pricePerNight, maxGuests, RoomStatus.AVAILABLE,
                "Premium room with terrace view and wellness amenities.");
    }

    @Override
    public String getRoomType() {
        return "Premium Room";
    }

    @Override
    public String getFeatures() {
        return "Terrace view, smart climate controls, spa access, dining table";
    }
}
