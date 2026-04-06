package com.yamaha.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Order;
import com.yamaha.entity.OrderItem;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, List<OrderItem> orderItems, Long addressId, String remark);

    void payOrder(Long orderId, Long userId);

    void cancelOrder(Long orderId, String cancelReason, Long userId);

    void deliveryOrder(Long orderId, Long userId);

    void receiveOrder(Long orderId, Long userId);

    Order getOrderDetail(Long orderId, Long userId);

    IPage<Order> getOrderPage(Long userId, Long pageNum, Long pageSize);

    List<OrderItem> getOrderItemsByOrderId(Long orderId, Long userId);
}
