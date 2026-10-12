package com.luxora.hotel.model;

public class DeluxeRoom extends Room {
    public DeluxeRoom(String roomNumber, double pricePerNight, int maxGuests) {
        super(roomNumber, pricePerNight, maxGuests, RoomStatus.AVAILABLE,
                "Premium deluxe suite with private balcony and elegant interiors.");
    }

    @Override
    public String getRoomType() {
        return "Deluxe Room";
    }

    @Override
    public String getFeatures() {
        return "King bed, balcony, lounge seating, city view, rain shower";
    }
}
