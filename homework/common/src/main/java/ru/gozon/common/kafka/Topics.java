package ru.gozon.common.kafka;

public final class Topics {
    private Topics() {}

    public static final String PAYMENT_REQUESTS = "orders.payment.request.v1";
    public static final String PAYMENT_RESULTS  = "payments.payment.result.v1";
}
