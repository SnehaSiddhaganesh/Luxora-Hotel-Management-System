package com.luxora.hotel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.FoodItem;
import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.Payment;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.PaymentType;

@Service
public class HotelOperationsServiceImpl implements HotelService {
    private final CustomerService customerService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final FoodItemService foodItemService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public HotelOperationsServiceImpl(CustomerService customerService,
                                     RoomService roomService,
                                     BookingService bookingService,
                                     FoodItemService foodItemService,
                                     OrderService orderService,
                                     PaymentService paymentService) {
        this.customerService = customerService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.foodItemService = foodItemService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        return customerService.registerCustomer(customer);
    }

    @Override
    public Room addRoom(Room room) {
        return roomService.saveRoom(room);
    }

    @Override
    public Booking createBooking(Long customerId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return bookingService.createBooking(customerId, roomId, checkInDate, checkOutDate, 1, null);
    }

    @Override
    public List<FoodItem> getAvailableMenuItems() {
        return foodItemService.getAvailableFoodItems();
    }

    @Override
    public Order placeFoodOrder(Long customerId, Long bookingId, List<com.luxora.hotel.entity.OrderItem> items) {
        return orderService.createOrder(customerId, bookingId, items);
    }

    @Override
    public Payment processPayment(Long bookingId, PaymentType paymentType, BigDecimal amount) {
        return paymentService.processPayment(bookingId, paymentType, amount);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }
}
