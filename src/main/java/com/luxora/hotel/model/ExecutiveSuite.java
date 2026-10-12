package com.luxora.hotel.model;

public class ExecutiveSuite extends Room {
    public ExecutiveSuite(String roomNumber, double pricePerNight, int maxGuests) {
        super(roomNumber, pricePerNight, maxGuests, RoomStatus.AVAILABLE,
                "Executive suite with private lounge and business-ready conveniences.");
    }

    @Override
    public String getRoomType() {
        return "Executive Suite";
    }

    @Override
    public String getFeatures() {
        return "Living area, work desk, marble bath, private lounge, concierge access";
    }
}
