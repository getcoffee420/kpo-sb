package ru.gozon.orders.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gozon.orders.domain.OrderEntity;
import ru.gozon.orders.repo.OrderRepository;
import ru.gozon.orders.service.OrdersService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService service;
    private final OrderRepository repo;

    public OrdersController(OrdersService service, OrderRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse create(@RequestHeader("user_id") String userId, @Valid @RequestBody CreateOrderRequest req) {
        OrderEntity order = service.createOrder(userId, req);
        return toResp(order);
    }

    @GetMapping
    public List<OrderResponse> list(@RequestHeader("user_id") String userId) {
        return repo.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResp).toList();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@RequestHeader("user_id") String userId, @PathVariable UUID id) {
        OrderEntity o = repo.findById(id)
                .filter(x -> x.getUserId().equals(userId))
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResp(o);
    }

    private OrderResponse toResp(OrderEntity o) {
        return new OrderResponse(o.getId(), o.getUserId(), o.getAmount(), o.getDescription(), o.getStatus(), o.getCreatedAt());
    }
}
