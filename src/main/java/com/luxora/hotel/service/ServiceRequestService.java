package com.luxora.hotel.service;

import java.util.List;

import com.luxora.hotel.entity.ServiceRequest;
import com.luxora.hotel.entity.ServiceRequestStatus;

public interface ServiceRequestService {
    ServiceRequest createServiceRequest(Long customerId, Long roomId, Long hotelServiceId, String notes);
    ServiceRequest updateStatus(Long serviceRequestId, ServiceRequestStatus status);
    List<ServiceRequest> getRequestsForCustomer(Long customerId);
    List<ServiceRequest> getRequestsForRoom(Long roomId);
    List<ServiceRequest> getRequestsByStatus(ServiceRequestStatus status);
}
