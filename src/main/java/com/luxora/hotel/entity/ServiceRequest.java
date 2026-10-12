package com.luxora.hotel.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "service_requests",
    indexes = {
        @Index(name = "idx_service_request_customer", columnList = "customer_id"),
        @Index(name = "idx_service_request_room", columnList = "room_id"),
        @Index(name = "idx_service_request_status", columnList = "status")
    }
)
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_service_request_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_service_request_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_service_id", nullable = false, foreignKey = @ForeignKey(name = "fk_service_request_hotel_service"))
    private HotelService hotelService;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column(length = 500)
    private String notes;

    public ServiceRequest() {
    }

    public ServiceRequest(Customer customer, Room room, HotelService hotelService, ServiceRequestStatus status, LocalDateTime requestedAt, String notes) {
        this.customer = customer;
        this.room = room;
        this.hotelService = hotelService;
        this.status = status != null ? status : ServiceRequestStatus.PENDING;
        this.requestedAt = requestedAt != null ? requestedAt : LocalDateTime.now();
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public HotelService getHotelService() {
        return hotelService;
    }

    public void setHotelService(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public ServiceRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
