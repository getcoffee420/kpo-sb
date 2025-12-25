package ru.gozon.orders.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull @Positive BigDecimal amount,
        @NotBlank String description
) {}
