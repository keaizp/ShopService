package com.yamaha.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Order;
import com.yamaha.entity.OrderItem;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, List<OrderItem> orderItems, Long addressId, String remark);

    void payOrder(Long orderId);

    void cancelOrder(Long orderId, String cancelReason);

    void deliveryOrder(Long orderId);

    void receiveOrder(Long orderId);

    Order getOrderDetail(Long orderId);

    IPage<Order> getOrderPage(Long userId, Long pageNum, Long pageSize);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}
