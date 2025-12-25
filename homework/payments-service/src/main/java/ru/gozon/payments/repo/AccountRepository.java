package ru.gozon.payments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gozon.payments.domain.AccountEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUserId(String userId);

    @Modifying
    @Query("update AccountEntity a set a.balance = a.balance + :amount where a.userId = :userId")
    int credit(@Param("userId") String userId, @Param("amount") BigDecimal amount);

    // атомарный debit: если не хватает денег, обновлений 0
    @Modifying
    @Query("update AccountEntity a set a.balance = a.balance - :amount " +
           "where a.userId = :userId and a.balance >= :amount")
    int debitIfEnough(@Param("userId") String userId, @Param("amount") BigDecimal amount);
}
