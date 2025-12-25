# Gozon

Асинхронное межсервисное взаимодействие с использованием Kafka  
с гарантией **at-least-once доставки сообщений** и  
**effectively exactly-once бизнес-семантикой** при списании денег за заказ.

## Сервисы

- **gateway** (8080) — Spring Cloud Gateway, маршрутизация запросов.
- **orders-service** (8081) — создание и просмотр заказов, **Transactional Outbox**.
- **payments-service** (8082) — управление счетами и балансом,  
  **Transactional Inbox + Outbox**, идемпотентная обработка платежей.

## Быстрый старт

```bash
docker compose up --build
```
- Gateway: http://localhost:8080
- Orders API + Swagger: http://localhost:8081/swagger-ui/index.html
- Payments API + Swagger: http://localhost:8082/swagger-ui/index.html

## Пример сценария

1) Создать счёт и пополнить:

```bash
curl -X POST http://localhost:8082/payments/account \
  -H 'user_id: u1'

curl -X POST http://localhost:8082/payments/topup \
  -H 'user_id: u1' \
  -H 'Content-Type: application/json' \
  -d '{"amount": 1000}'

curl http://localhost:8082/payments/balance \
  -H 'user_id: u1'
```

2) Создать заказ (асинхронно запустит оплату):

```bash
curl -X POST http://localhost:8081/orders \
  -H 'user_id: u1' \
  -H 'Content-Type: application/json' \
  -d '{"amount": 200, "description": "sweater with deers"}'
```

3) Проверить статус:

```bash
curl http://localhost:8081/orders \
  -H 'user_id: u1'
```
