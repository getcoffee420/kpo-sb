package ru.gozon.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox",
       indexes = @Index(name = "idx_outbox_status_created", columnList = "status, createdAt"))
public class OutboxMessage {
    public enum Status { NEW, SENT }

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String key;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant sentAt;

    protected OutboxMessage() {}

    public OutboxMessage(UUID id, String topic, String key, String payloadJson) {
        this.id = id;
        this.topic = topic;
        this.key = key;
        this.payloadJson = payloadJson;
        this.status = Status.NEW;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getKey() { return key; }
    public String getPayloadJson() { return payloadJson; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void markSent() {
        this.status = Status.SENT;
        this.sentAt = Instant.now();
    }
}
