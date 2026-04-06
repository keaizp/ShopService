package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Order;
import com.yamaha.entity.OrderItem;
import com.yamaha.mapper.OrderItemMapper;
import com.yamaha.mapper.OrderMapper;
import com.yamaha.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> orderItems, Long addressId, String remark) {
        log.info("创建订单, 用户ID: {}, 商品数量: {}, 地址ID: {}", userId, orderItems.size(), addressId);
        // 生成订单编号
        String orderNo = generateOrderNo();
        log.info("生成订单编号: {}", orderNo);
        
        // 计算订单金额
        BigDecimal totalAmount = calculateTotalAmount(orderItems);
        log.info("订单总金额: {}", totalAmount);
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setStatus(0); // 待支付
        order.setAddressId(addressId);
        order.setRemark(remark);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        this.save(order);
        log.info("订单保存成功, 订单ID: {}", order.getId());
        
        // 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }
        log.info("订单明细保存成功, 共 {} 项", orderItems.size());
        
        return order;
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, Long userId) {
        log.info("支付订单, 订单ID: {}, 用户ID: {}", orderId, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getStatus() == 0 && order.getUserId().equals(userId)) {
            order.setStatus(1); // 已支付
            order.setPayType(1); // 微信支付
            order.setPayTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
            log.info("订单支付成功, 订单编号: {}", order.getOrderNo());
        } else {
            log.warn("订单支付失败, 订单ID: {}, 用户ID: {}, 状态: {}", orderId, userId, order != null ? order.getStatus() : "订单不存在");
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String cancelReason, Long userId) {
        log.info("取消订单, 订单ID: {}, 原因: {}, 用户ID: {}", orderId, cancelReason, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getStatus() == 0 && order.getUserId().equals(userId)) {
            order.setStatus(4); // 已取消
            order.setCancelReason(cancelReason);
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
            log.info("订单取消成功, 订单编号: {}", order.getOrderNo());
        } else {
            log.warn("订单取消失败, 订单ID: {}, 用户ID: {}, 状态: {}", orderId, userId, order != null ? order.getStatus() : "订单不存在");
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    @Override
    @Transactional
    public void deliveryOrder(Long orderId, Long userId) {
        log.info("发货订单, 订单ID: {}, 用户ID: {}", orderId, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getStatus() == 1 && order.getUserId().equals(userId)) {
            order.setStatus(2); // 已发货
            order.setDeliveryTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
            log.info("订单发货成功, 订单编号: {}", order.getOrderNo());
        } else {
            log.warn("订单发货失败, 订单ID: {}, 用户ID: {}, 状态: {}", orderId, userId, order != null ? order.getStatus() : "订单不存在");
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    @Override
    @Transactional
    public void receiveOrder(Long orderId, Long userId) {
        log.info("确认收货, 订单ID: {}, 用户ID: {}", orderId, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getStatus() == 2 && order.getUserId().equals(userId)) {
            order.setStatus(3); // 已完成
            order.setReceiveTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
            log.info("订单收货成功, 订单编号: {}", order.getOrderNo());
        } else {
            log.warn("订单收货失败, 订单ID: {}, 用户ID: {}, 状态: {}", orderId, userId, order != null ? order.getStatus() : "订单不存在");
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    @Override
    public Order getOrderDetail(Long orderId, Long userId) {
        log.info("获取订单详情, 订单ID: {}, 用户ID: {}", orderId, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getUserId().equals(userId)) {
            return order;
        } else {
            log.warn("获取订单详情失败, 订单ID: {}, 用户ID: {}", orderId, userId);
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    @Override
    public IPage<Order> getOrderPage(Long userId, Long pageNum, Long pageSize) {
        log.info("获取用户订单列表, 用户ID: {}", userId);
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId, Long userId) {
        log.info("获取订单商品列表, 订单ID: {}, 用户ID: {}", orderId, userId);
        Order order = this.getById(orderId);
        if (order != null && order.getUserId().equals(userId)) {
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderId, orderId);
            return orderItemMapper.selectList(wrapper);
        } else {
            log.warn("获取订单商品列表失败, 订单ID: {}, 用户ID: {}", orderId, userId);
            throw new RuntimeException("订单不存在或无权限操作");
        }
    }

    private String generateOrderNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "ORD" + timestamp + random;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}
