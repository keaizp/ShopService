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
    public Result<Order> create(@RequestBody OrderCreateDTO orderCreateDTO, @RequestAttribute("userId") Long userId) {
        if (!orderCreateDTO.getUserId().equals(userId)) {
            return Result.error("无权创建其他用户的订单");
        }
        Order order = orderService.createOrder(
            userId,
            orderCreateDTO.getOrderItems(),
            orderCreateDTO.getAddressId(),
            orderCreateDTO.getRemark()
        );
        return Result.success(order);
    }

    @PutMapping("/{id}/pay")
    public Result<Boolean> pay(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        orderService.payOrder(id, userId);
        return Result.success(true);
    }

    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id, @RequestBody OrderCancelDTO orderCancelDTO, @RequestAttribute("userId") Long userId) {
        orderService.cancelOrder(id, orderCancelDTO.getCancelReason(), userId);
        return Result.success(true);
    }

    @PutMapping("/{id}/delivery")
    public Result<Boolean> delivery(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        orderService.deliveryOrder(id, userId);
        return Result.success(true);
    }

    @PutMapping("/{id}/receive")
    public Result<Boolean> receive(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        orderService.receiveOrder(id, userId);
        return Result.success(true);
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        return Result.success(orderService.getOrderDetail(id, userId));
    }

    @GetMapping("/items/{orderId}")
    public Result<List<OrderItem>> getItems(@PathVariable Long orderId, @RequestAttribute("userId") Long userId) {
        return Result.success(orderService.getOrderItemsByOrderId(orderId, userId));
    }

    @GetMapping("/list")
    public Result<IPage<Order>> list(@RequestParam("userId") Long userId, PageDTO pageDTO, @RequestAttribute("userId") Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            return Result.error("无权查看其他用户的订单");
        }
        return Result.success(orderService.getOrderPage(userId, pageDTO.getPageNum(), pageDTO.getPageSize()));
    }
}
