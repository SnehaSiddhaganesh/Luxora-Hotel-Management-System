package com.luxora.hotel.service;

public interface LaundryService {
    boolean scheduleLaundry(String guestName, int bagCount);
    boolean markLaundryComplete(String guestName);
}
