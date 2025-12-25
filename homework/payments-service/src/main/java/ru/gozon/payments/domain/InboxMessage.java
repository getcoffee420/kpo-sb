package ru.gozon.payments.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox", uniqueConstraints = @UniqueConstraint(name = "uk_inbox_event", columnNames = "eventId"))
public class InboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Instant receivedAt;

    protected InboxMessage() {}

    public InboxMessage(UUID eventId, UUID orderId, String type) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.type = type;
        this.receivedAt = Instant.now();
    }
}
