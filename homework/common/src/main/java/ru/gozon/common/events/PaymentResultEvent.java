package ru.gozon.common.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentResultEvent(
        UUID eventId,
        UUID orderId,
        String userId,
        PaymentStatus status,
        String reason,
        Instant createdAt
) {}

