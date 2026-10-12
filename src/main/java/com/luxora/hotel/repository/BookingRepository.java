package com.luxora.hotel.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Booking;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.model.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Override
    @EntityGraph(attributePaths = {"customer", "room"})
    Optional<Booking> findById(Long bookingId);

    @Override
    @EntityGraph(attributePaths = {"customer", "room"})
    List<Booking> findAll();

    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByCustomer(Customer customer);
    @EntityGraph(attributePaths = {"customer", "room"})
    List<Booking> findByCustomerOrderByCheckInDateDesc(Customer customer);
    List<Booking> findByCustomerAndStatus(Customer customer, BookingStatus status);
    List<Booking> findByRoom(Room room);
    List<Booking> findByRoomAndStatus(Room room, BookingStatus status);
    @EntityGraph(attributePaths = {"customer", "room"})
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStatusIn(Collection<BookingStatus> statuses);
        boolean existsByRoomAndStatusNotInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Room room, Collection<BookingStatus> excludedStatuses, LocalDate requestedCheckOut, LocalDate requestedCheckIn);
    List<Booking> findByCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(LocalDate checkOutDate, LocalDate checkInDate);
    boolean existsByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(Room room, LocalDate newCheckOutDate, LocalDate newCheckInDate);
}
