package com.luxora.hotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.entity.HotelService;
import com.luxora.hotel.entity.Room;
import com.luxora.hotel.entity.ServiceRequest;
import com.luxora.hotel.entity.ServiceRequestStatus;
import com.luxora.hotel.repository.CustomerRepository;
import com.luxora.hotel.repository.HotelServiceRepository;
import com.luxora.hotel.repository.RoomRepository;
import com.luxora.hotel.repository.ServiceRequestRepository;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;
    private final HotelServiceRepository hotelServiceRepository;

    public ServiceRequestServiceImpl(ServiceRequestRepository serviceRequestRepository,
                                    CustomerRepository customerRepository,
                                    RoomRepository roomRepository,
                                    HotelServiceRepository hotelServiceRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @Override
    @Transactional
    public ServiceRequest createServiceRequest(Long customerId, Long roomId, Long hotelServiceId, String notes) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer id is required.");
        }
        if (hotelServiceId == null) {
            throw new IllegalArgumentException("Hotel service is required.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
        HotelService hotelService = hotelServiceRepository.findById(hotelServiceId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel service not found with id: " + hotelServiceId));

        Room room = null;
        if (roomId != null) {
            room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        }

        ServiceRequest request = new ServiceRequest(customer, room, hotelService, ServiceRequestStatus.PENDING, LocalDateTime.now(), notes);
        return serviceRequestRepository.save(request);
    }

    @Override
    @Transactional
    public ServiceRequest updateStatus(Long serviceRequestId, ServiceRequestStatus status) {
        if (serviceRequestId == null) {
            throw new IllegalArgumentException("Service request id is required.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        ServiceRequest request = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with id: " + serviceRequestId));
        request.setStatus(status);
        return serviceRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getRequestsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));
        return serviceRequestRepository.findByCustomerOrderByRequestedAtDesc(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getRequestsForRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        return serviceRequestRepository.findByRoom(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getRequestsByStatus(ServiceRequestStatus status) {
        return serviceRequestRepository.findByStatus(status);
    }
}
