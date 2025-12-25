package ru.gozon.payments.api;

import java.math.BigDecimal;

public record BalanceResponse(
        String userId,
        BigDecimal balance
) {}
