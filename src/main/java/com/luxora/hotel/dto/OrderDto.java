package com.luxora.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.luxora.hotel.entity.Order;
import com.luxora.hotel.entity.OrderItem;
import com.luxora.hotel.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderDto {

    public record CreateRequest(
            @NotNull(message = "Customer id is required") Long customerId,
            @NotNull(message = "Booking id is required") Long bookingId,
            @NotNull(message = "Order items are required") @Valid List<OrderItemRequest> items
    ) {
    }

    public record OrderItemRequest(
            @NotNull(message = "Food item id is required") Long foodItemId,
            @Positive(message = "Quantity must be positive") int quantity
    ) {
    }

    public record StatusUpdateRequest(@NotNull(message = "Status is required") OrderStatus status) {
    }

    public record OrderItemResponse(
            Long id,
            Long foodItemId,
            String foodItemName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
        public static OrderItemResponse fromEntity(OrderItem item) {
            if (item == null) {
                return null;
            }

            return new OrderItemResponse(
                    item.getId(),
                    item.getFoodItem() != null ? item.getFoodItem().getId() : null,
                    item.getFoodItem() != null ? item.getFoodItem().getName() : null,
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getLineTotal()
            );
        }
    }

    public record Response(
            Long id,
            String orderCode,
            Long customerId,
            Long bookingId,
            String roomNumber,
            OrderStatus status,
            LocalDateTime orderedAt,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total,
            List<OrderItemResponse> items
    ) {
        public static Response fromEntity(Order order) {
            if (order == null) {
                return null;
            }

            List<OrderItemResponse> items = order.getItems() == null
                    ? List.of()
                    : order.getItems().stream().map(OrderItemResponse::fromEntity).toList();

            return new Response(
                    order.getId(),
                    order.getOrderCode(),
                    order.getCustomer() != null ? order.getCustomer().getId() : null,
                    order.getBooking() != null ? order.getBooking().getId() : null,
                    order.getBooking() != null && order.getBooking().getRoom() != null
                            ? order.getBooking().getRoom().getRoomNumber() : null,
                    order.getStatus(),
                    order.getOrderedAt(),
                    order.getSubtotal(),
                    order.getTax(),
                    order.getTotal(),
                    items
            );
        }
    }
}
