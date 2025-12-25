package ru.gozon.payments.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.gozon.common.events.PaymentRequestEvent;
import ru.gozon.common.kafka.Topics;
import ru.gozon.payments.service.PaymentProcessor;

@Component
public class PaymentRequestListener {

    private final ObjectMapper mapper;
    private final PaymentProcessor processor;

    public PaymentRequestListener(ObjectMapper mapper, PaymentProcessor processor) {
        this.mapper = mapper;
        this.processor = processor;
    }

    @KafkaListener(topics = Topics.PAYMENT_REQUESTS, groupId = "payments-service")
    @Transactional
    public void onMessage(String payloadJson, Acknowledgment ack) throws Exception {
        PaymentRequestEvent event = mapper.readValue(payloadJson, PaymentRequestEvent.class);
        processor.handle(event);
        ack.acknowledge();
    }
}
