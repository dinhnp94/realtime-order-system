package com.orders.order_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * CreateOrderRequest — the HTTP request body for POST /orders.
 *
 * @NotBlank, @Min, @Max are validation annotations.
 * If the client sends invalid data, Spring automatically rejects it with a 400 error
 * before our code even runs. No manual if-checks needed.
 */
@Data
public class CreateOrderRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "productId is required")
    private String productId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    @Max(value = 100, message = "quantity cannot exceed 100 per order")
    private Integer quantity;
}
