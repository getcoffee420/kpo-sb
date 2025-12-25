package ru.gozon.payments.api;

public class NoAccountException extends RuntimeException {
    public NoAccountException(String userId) {
        super("Account not found for user_id=" + userId + ". Create it via POST /payments/account");
    }
}
