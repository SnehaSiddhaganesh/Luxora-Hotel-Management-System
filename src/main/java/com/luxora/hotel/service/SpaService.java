package com.luxora.hotel.service;

public interface SpaService {
    boolean bookSpaSession(String guestName, String serviceType);
    boolean cancelSpaSession(String guestName);
}
