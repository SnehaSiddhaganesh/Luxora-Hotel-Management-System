package com.luxora.hotel.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.HotelService;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.entity.ServiceRequest;
import com.luxora.hotel.entity.ServiceRequestStatus;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCustomer(Customer customer);
    List<ServiceRequest> findByCustomerOrderByRequestedAtDesc(Customer customer);
    List<ServiceRequest> findByCustomerAndStatus(Customer customer, ServiceRequestStatus status);
    List<ServiceRequest> findByRoom(Room room);
    List<ServiceRequest> findByRoomAndStatus(Room room, ServiceRequestStatus status);
    List<ServiceRequest> findByStatus(ServiceRequestStatus status);
    List<ServiceRequest> findByStatusIn(Collection<ServiceRequestStatus> statuses);
    List<ServiceRequest> findByHotelService(HotelService hotelService);
}