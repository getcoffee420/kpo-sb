package ru.gozon.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.gozon.common.events.PaymentRequestEvent;
import ru.gozon.common.events.PaymentResultEvent;
import ru.gozon.common.events.PaymentStatus;
import ru.gozon.common.kafka.Topics;
import ru.gozon.payments.domain.InboxMessage;
import ru.gozon.payments.domain.OutboxMessage;
import ru.gozon.payments.repo.AccountRepository;
import ru.gozon.payments.repo.InboxRepository;
import ru.gozon.payments.repo.OutboxRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentProcessor {

    private final InboxRepository inbox;
    private final AccountRepository accounts;
    private final OutboxRepository outbox;
    private final ObjectMapper mapper;

    public PaymentProcessor(InboxRepository inbox, AccountRepository accounts, OutboxRepository outbox, ObjectMapper mapper) {
        this.inbox = inbox;
        this.accounts = accounts;
        this.outbox = outbox;
        this.mapper = mapper;
    }

    /**
     * Transactional Inbox + Outbox:
     *  - dedupe по eventId (сообщение из Orders)
     *  - атомарный debit (effectively exactly once по заказу)
     *  - запись результата в outbox в рамках той же транзакции
     */
    @Transactional
    public void handle(PaymentRequestEvent ev) throws Exception {
        if (inbox.existsByEventId(ev.eventId())) {
            return; // уже обработали
        }
        inbox.save(new InboxMessage(ev.eventId(), ev.orderId(), "PaymentRequestEvent"));

        PaymentStatus status;
        String reason = null;

        var accountOpt = accounts.findByUserId(ev.userId());
        if (accountOpt.isEmpty()) {
            status = PaymentStatus.FAILED;
            reason = "NO_ACCOUNT";
        } else {
            int updated = accounts.debitIfEnough(ev.userId(), ev.amount());
            if (updated == 1) {
                status = PaymentStatus.SUCCESS;
            } else {
                status = PaymentStatus.FAILED;
                reason = "NOT_ENOUGH_MONEY";
            }
        }

        PaymentResultEvent result = new PaymentResultEvent(
                UUID.randomUUID(),
                ev.orderId(),
                ev.userId(),
                status,
                reason,
                Instant.now()
        );

        String payload = mapper.writeValueAsString(result);
        outbox.save(new OutboxMessage(UUID.randomUUID(), Topics.PAYMENT_RESULTS, ev.orderId().toString(), payload));
    }
}
