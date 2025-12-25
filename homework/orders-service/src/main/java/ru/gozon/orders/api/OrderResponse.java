package ru.gozon.orders.api;

import ru.gozon.orders.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String userId,
        BigDecimal amount,
        String description,
        OrderStatus status,
        Instant createdAt
) {}
