package ru.gozon.orders.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.gozon.common.events.PaymentResultEvent;
import ru.gozon.common.events.PaymentStatus;
import ru.gozon.common.kafka.Topics;
import ru.gozon.orders.domain.OrderEntity;
import ru.gozon.orders.domain.OrderStatus;
import ru.gozon.orders.repo.OrderRepository;

import jakarta.transaction.Transactional;

@Component
public class PaymentResultListener {

    private final ObjectMapper mapper;
    private final OrderRepository orders;

    public PaymentResultListener(ObjectMapper mapper, OrderRepository orders) {
        this.mapper = mapper;
        this.orders = orders;
    }

    @KafkaListener(topics = Topics.PAYMENT_RESULTS, groupId = "orders-service")
    @Transactional
    public void onMessage(String payloadJson, Acknowledgment ack) throws Exception {
        PaymentResultEvent event = mapper.readValue(payloadJson, PaymentResultEvent.class);

        OrderEntity order = orders.findById(event.orderId()).orElse(null);
        if (order == null) {
            ack.acknowledge();
            return;
        }

        // Идемпотентность обновления: повтор одного и того же результата просто приведёт к тем же статусам.
        if (event.status() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.FINISHED);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        ack.acknowledge();
    }
}
