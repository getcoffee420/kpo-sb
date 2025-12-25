package ru.gozon.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.gozon.common.events.PaymentRequestEvent;
import ru.gozon.common.kafka.Topics;
import ru.gozon.orders.api.CreateOrderRequest;
import ru.gozon.orders.domain.OrderEntity;
import ru.gozon.orders.domain.OrderStatus;
import ru.gozon.orders.domain.OutboxMessage;
import ru.gozon.orders.repo.OrderRepository;
import ru.gozon.orders.repo.OutboxRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrdersService {

    private final OrderRepository orders;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;

    public OrdersService(OrderRepository orders, OutboxRepository outbox, ObjectMapper mapper) {
        this.orders = orders;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Transactional
    public OrderEntity createOrder(String userId, CreateOrderRequest req) {
        UUID orderId = UUID.randomUUID();

        OrderEntity order = new OrderEntity(
                orderId,
                userId,
                req.amount(),
                req.description(),
                OrderStatus.NEW,
                Instant.now()
        );
        orders.save(order);

        PaymentRequestEvent event = new PaymentRequestEvent(
                UUID.randomUUID(),
                orderId,
                userId,
                req.amount(),
                req.description(),
                Instant.now()
        );

        String payload;
        try {
            payload = mapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize event", e);
        }

        // Transactional Outbox: сохраняем "задачу на отправку" в рамках той же транзакции, что и заказ.
        outbox.save(new OutboxMessage(UUID.randomUUID(), Topics.PAYMENT_REQUESTS, orderId.toString(), payload));

        return order;
    }
}
