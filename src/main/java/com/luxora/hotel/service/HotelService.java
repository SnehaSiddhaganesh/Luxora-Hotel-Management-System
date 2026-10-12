package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.PaymentType;

public interface HotelService {
    Customer registerCustomer(Customer customer);
    Room addRoom(Room room);
    Booking createBooking(Long customerId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
    List<FoodItem> getAvailableMenuItems();
    Order placeFoodOrder(Long customerId, Long bookingId, List<com.luxora.hotel.entity.OrderItem> items);
    Payment processPayment(Long bookingId, PaymentType paymentType, BigDecimal amount);
    List<Room> getAvailableRooms();
}
