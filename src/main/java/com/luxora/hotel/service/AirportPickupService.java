package com.luxora.hotel.service;

public interface AirportPickupService {
    boolean schedulePickup(String guestName, String pickupTime);
    boolean cancelPickup(String guestName);
}
