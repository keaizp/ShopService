package com.yamaha.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yamaha.common.PageDTO;
import com.yamaha.common.Result;
import com.yamaha.dto.OrderCancelDTO;
import com.yamaha.dto.OrderCreateDTO;
import com.yamaha.entity.Order;
import com.yamaha.entity.OrderItem;
import com.yamaha.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<Order> create(@RequestBody OrderCreateDTO orderCreateDTO) {
        Order order = orderService.createOrder(
            orderCreateDTO.getUserId(),
            orderCreateDTO.getOrderItems(),
            orderCreateDTO.getAddressId(),
            orderCreateDTO.getRemark()
        );
        return Result.success(order);
    }

    @PutMapping("/{id}/pay")
    public Result<Boolean> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success(true);
    }

    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id,
                                @RequestBody OrderCancelDTO orderCancelDTO) {
        orderService.cancelOrder(id, orderCancelDTO.getCancelReason());
        return Result.success(true);
    }

    @PutMapping("/{id}/delivery")
    public Result<Boolean> delivery(@PathVariable Long id) {
        orderService.deliveryOrder(id);
        return Result.success(true);
    }

    @PutMapping("/{id}/receive")
    public Result<Boolean> receive(@PathVariable Long id) {
        orderService.receiveOrder(id);
        return Result.success(true);
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @GetMapping("/items/{orderId}")
    public Result<List<OrderItem>> getItems(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/list")
    public Result<IPage<Order>> list(@RequestParam("userId") Long userId,
                                   PageDTO pageDTO) {
        return Result.success(orderService.getOrderPage(userId, pageDTO.getPageNum(), pageDTO.getPageSize()));
    }
}
