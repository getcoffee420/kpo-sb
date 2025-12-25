package ru.gozon.orders.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gozon.orders.domain.OutboxMessage;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query("select m from OutboxMessage m where m.status = 'NEW' order by m.createdAt asc")
    List<OutboxMessage> findBatchNew(Pageable pageable);
}
