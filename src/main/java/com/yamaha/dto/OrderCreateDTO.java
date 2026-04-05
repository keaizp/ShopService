package com.yamaha.dto;

import com.yamaha.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    private Long userId;
    private List<OrderItem> orderItems;
    private Long addressId;
    private String remark;
}
