package ru.gozon.payments.api;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gozon.payments.domain.AccountEntity;
import ru.gozon.payments.repo.AccountRepository;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private final AccountRepository accounts;

    public PaymentsController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    // 1) Создание счета (не более одного на user)
    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceResponse createAccount(@RequestHeader("user_id") String userId) {
        AccountEntity a = accounts.findByUserId(userId).orElseGet(() -> accounts.save(new AccountEntity(userId)));
        return new BalanceResponse(userId, a.getBalance());
    }

    // 2) Пополнение счета
    @PostMapping("/topup")
    @Transactional
    public BalanceResponse topUp(@RequestHeader("user_id") String userId, @Valid @RequestBody TopUpRequest req) {
        AccountEntity a = accounts.findByUserId(userId).orElseThrow(() -> new NoAccountException(userId));
        accounts.credit(userId, req.amount());
        BigDecimal newBalance = accounts.findByUserId(userId).orElseThrow().getBalance();
        return new BalanceResponse(userId, newBalance);
    }

    // 3) Просмотр баланса
    @GetMapping("/balance")
    public BalanceResponse balance(@RequestHeader("user_id") String userId) {
        AccountEntity a = accounts.findByUserId(userId).orElseThrow(() -> new NoAccountException(userId));
        return new BalanceResponse(userId, a.getBalance());
    }
}
