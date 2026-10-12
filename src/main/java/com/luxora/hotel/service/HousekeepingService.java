package com.luxora.hotel.service;

public interface HousekeepingService {
    boolean requestRoomCleaning(String roomNumber);
    boolean scheduleTurnDownService(String roomNumber);
}
