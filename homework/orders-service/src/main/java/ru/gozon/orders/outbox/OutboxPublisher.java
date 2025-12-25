package ru.gozon.orders.outbox;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.gozon.orders.domain.OutboxMessage;
import ru.gozon.orders.repo.OutboxRepository;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxRepository outbox;
    private final KafkaTemplate<String, Object> kafka;
    private final int batchSize;

    public OutboxPublisher(OutboxRepository outbox,
                           KafkaTemplate<String, Object> kafka,
                           @Value("${app.outbox.batch-size:50}") int batchSize) {
        this.outbox = outbox;
        this.kafka = kafka;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:500}")
    @Transactional
    public void publish() {
        List<OutboxMessage> batch = outbox.findBatchNew(PageRequest.of(0, batchSize));
        for (OutboxMessage msg : batch) {
            try {
                kafka.send(msg.getTopic(), msg.getKey(), msg.getPayloadJson()).get();
                msg.markSent();
                // entity managed in tx, update occurs automatically
            } catch (Exception e) {
                // оставляем NEW, попробуем позже (at-least-once)
                return;
            }
        }
    }
}
