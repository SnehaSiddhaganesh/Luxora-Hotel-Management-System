package com.luxora.hotel.model;

public abstract class Room {
    private final String roomNumber;
    private double pricePerNight;
    private int maxGuests;
    private RoomStatus status;
    private String description;

    protected Room(String roomNumber, double pricePerNight, int maxGuests, RoomStatus status, String description) {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Room number cannot be blank.");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive.");
        }
        if (maxGuests <= 0) {
            throw new IllegalArgumentException("Maximum guests must be greater than zero.");
        }

        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.status = status != null ? status : RoomStatus.AVAILABLE;
        this.description = description != null ? description : "Luxury accommodation";
    }

    public abstract String getRoomType();

    public abstract String getFeatures();

    public double calculateStayCost(int nights) {
        if (nights < 1) {
            throw new IllegalArgumentException("Nights must be at least 1.");
        }
        return pricePerNight * nights;
    }

    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE || status == RoomStatus.RESERVED;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight > 0) {
            this.pricePerNight = pricePerNight;
        }
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        if (maxGuests > 0) {
            this.maxGuests = maxGuests;
        }
    }

    public RoomStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getRoomType() + " #" + roomNumber + " | Status: " + status;
    }
}
